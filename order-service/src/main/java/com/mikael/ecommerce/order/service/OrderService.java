package com.mikael.ecommerce.order.service;

import com.mikael.ecommerce.order.client.ProductClient;
import com.mikael.ecommerce.order.dto.*;
import com.mikael.ecommerce.order.entity.Order;
import com.mikael.ecommerce.order.entity.OrderItem;
import com.mikael.ecommerce.order.entity.OrderStatus;
import com.mikael.ecommerce.order.repository.OrderRepository;
import com.mikael.ecommerce.order.repository.projection.BestSellerProjection;
import com.mikael.ecommerce.order.repository.projection.VipCustomerProjection;
import com.mikael.ecommerce.order.strategy.ShippingFeeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final ShippingFeeCalculator shippingFeeCalculator;

    @Transactional
    public OrderResponse checkoutOrder(CreateOrderRequest request) {
        log.info("Starting checkout for customer: {}, items count: {}",
                request.getCustomerEmail(), request.getItems().size());

        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemRequest> reservedItems = new ArrayList<>();

        try {
            for (OrderItemRequest itemReq : request.getItems()) {
                ProductDto product = productClient.getProductById(itemReq.getProductId());

                productClient.reserveStock(product.getId(), itemReq.getQuantity());
                reservedItems.add(itemReq);

                BigDecimal unitPrice = product.getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP);

                OrderItem orderItem = OrderItem.builder()
                        .productId(product.getId())
                        .sku(product.getSku())
                        .productName(product.getName())
                        .category(product.getCategory())
                        .unitPrice(unitPrice)
                        .quantity(itemReq.getQuantity())
                        .subtotal(subtotal)
                        .unitWeightKg(product.getWeightKg())
                        .build();

                orderItems.add(orderItem);
            }
        } catch (Exception ex) {
            log.error("Checkout failed, initiating stock rollback: {}", ex.getMessage());
            for (OrderItemRequest reserved : reservedItems) {
                productClient.releaseStock(reserved.getProductId(), reserved.getQuantity());
            }
            throw new RuntimeException("Checkout failed: " + ex.getMessage(), ex);
        }

        BigDecimal totalWeightKg = orderItems.stream()
                .map(item -> item.getUnitWeightKg().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal itemsSubtotal = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal shippingFee = shippingFeeCalculator.calculate(
                request.getShippingType(),
                totalWeightKg,
                itemsSubtotal
        );

        BigDecimal totalAmount = itemsSubtotal.add(shippingFee).setScale(2, RoundingMode.HALF_UP);

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerEmail(request.getCustomerEmail())
                .shippingType(request.getShippingType().toUpperCase())
                .shippingFee(shippingFee)
                .itemsSubtotal(itemsSubtotal)
                .totalWeightKg(totalWeightKg)
                .totalAmount(totalAmount)
                .status(OrderStatus.COMPLETED)
                .build();

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order successfully created: id={}, orderNumber={}, totalAmount={}",
                savedOrder.getId(), savedOrder.getOrderNumber(), savedOrder.getTotalAmount());

        return OrderResponse.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<BestSellerProjection> getBestSellers(int topN) {
        return orderRepository.findBestSellerProductsPerCategory(topN);
    }

    @Transactional(readOnly = true)
    public List<VipCustomerProjection> getVipCustomers(long minOrders, BigDecimal minSpend) {
        return orderRepository.findVipCustomers(minOrders, minSpend);
    }
}
