package com.mikael.ecommerce.order.config;

import com.mikael.ecommerce.order.entity.Order;
import com.mikael.ecommerce.order.entity.OrderItem;
import com.mikael.ecommerce.order.entity.OrderStatus;
import com.mikael.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderDataSeeder implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        if (orderRepository.count() > 0) {
            log.info("Orders already seeded.");
            return;
        }

        Order order1 = Order.builder()
                .orderNumber("ORD-ELEC-1001")
                .customerEmail("alice.johnson@example.com")
                .shippingType("EXPRESS")
                .shippingFee(new BigDecimal("40000.00"))
                .itemsSubtotal(new BigDecimal("12500000.00"))
                .totalWeightKg(new BigDecimal("1.45"))
                .totalAmount(new BigDecimal("12540000.00"))
                .status(OrderStatus.COMPLETED)
                .build();
        order1.addItem(OrderItem.builder()
                .productId(1L)
                .sku("ELEC-LPTP-001")
                .productName("Laptop Ultra 14-inch")
                .category("ELECTRONICS")
                .unitPrice(new BigDecimal("12500000.00"))
                .quantity(1)
                .subtotal(new BigDecimal("12500000.00"))
                .unitWeightKg(new BigDecimal("1.45"))
                .build());

        Order order2 = Order.builder()
                .orderNumber("ORD-FASH-1002")
                .customerEmail("alice.johnson@example.com")
                .shippingType("REGULAR")
                .shippingFee(new BigDecimal("20000.00"))
                .itemsSubtotal(new BigDecimal("1030000.00"))
                .totalWeightKg(new BigDecimal("1.50"))
                .totalAmount(new BigDecimal("1050000.00"))
                .status(OrderStatus.COMPLETED)
                .build();
        order2.addItem(OrderItem.builder()
                .productId(4L)
                .sku("FASH-JACK-001")
                .productName("Waterproof Windbreaker Jacket")
                .category("FASHION")
                .unitPrice(new BigDecimal("450000.00"))
                .quantity(1)
                .subtotal(new BigDecimal("450000.00"))
                .unitWeightKg(new BigDecimal("0.40"))
                .build());
        order2.addItem(OrderItem.builder()
                .productId(6L)
                .sku("BOOK-JAVA-001")
                .productName("Mastering Spring Boot & Microservices")
                .category("BOOKS")
                .unitPrice(new BigDecimal("280000.00"))
                .quantity(2)
                .subtotal(new BigDecimal("560000.00"))
                .unitWeightKg(new BigDecimal("0.65"))
                .build());

        Order order3 = Order.builder()
                .orderNumber("ORD-CRGO-1003")
                .customerEmail("bob.smith@example.com")
                .shippingType("CARGO")
                .shippingFee(new BigDecimal("90000.00"))
                .itemsSubtotal(new BigDecimal("3800000.00"))
                .totalWeightKg(new BigDecimal("15.20"))
                .totalAmount(new BigDecimal("3890000.00"))
                .status(OrderStatus.COMPLETED)
                .build();
        order3.addItem(OrderItem.builder()
                .productId(8L)
                .sku("FURN-CHAIR-001")
                .productName("Ergonomic Mesh Office Chair")
                .category("FURNITURE")
                .unitPrice(new BigDecimal("1950000.00"))
                .quantity(1)
                .subtotal(new BigDecimal("1950000.00"))
                .unitWeightKg(new BigDecimal("14.50"))
                .build());
        order3.addItem(OrderItem.builder()
                .productId(3L)
                .sku("ELEC-HEAD-003")
                .productName("Noise-Cancelling Headphones")
                .category("ELECTRONICS")
                .unitPrice(new BigDecimal("1850000.00"))
                .quantity(1)
                .subtotal(new BigDecimal("1850000.00"))
                .unitWeightKg(new BigDecimal("0.35"))
                .build());

        Order order4 = Order.builder()
                .orderNumber("ORD-FASH-1004")
                .customerEmail("charlie.brown@example.com")
                .shippingType("REGULAR")
                .shippingFee(new BigDecimal("10000.00"))
                .itemsSubtotal(new BigDecimal("750000.00"))
                .totalWeightKg(new BigDecimal("0.85"))
                .totalAmount(new BigDecimal("760000.00"))
                .status(OrderStatus.COMPLETED)
                .build();
        order4.addItem(OrderItem.builder()
                .productId(5L)
                .sku("FASH-SHOE-002")
                .productName("Ergonomic Running Shoes")
                .category("FASHION")
                .unitPrice(new BigDecimal("750000.00"))
                .quantity(1)
                .subtotal(new BigDecimal("750000.00"))
                .unitWeightKg(new BigDecimal("0.85"))
                .build());

        orderRepository.saveAll(List.of(order1, order2, order3, order4));
        log.info("Successfully seeded 4 orders with order items for initial analytics.");
    }
}
