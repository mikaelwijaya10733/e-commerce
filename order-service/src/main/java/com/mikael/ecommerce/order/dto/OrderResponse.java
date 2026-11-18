package com.mikael.ecommerce.order.dto;

import com.mikael.ecommerce.order.entity.Order;
import com.mikael.ecommerce.order.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private CustomerResponse customer;
    private String customerEmail;
    private String shippingType;
    private BigDecimal shippingFee;
    private BigDecimal itemsSubtotal;
    private BigDecimal totalWeightKg;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderDetailResponse> details;
    private List<OrderDetailResponse> items;
    private LocalDateTime createdAt;

    public static OrderResponse fromEntity(Order order) {
        CustomerResponse customerResp = CustomerResponse.fromEntity(order.getCustomer());
        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream()
                .map(OrderDetailResponse::fromEntity)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customer(customerResp)
                .customerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null)
                .shippingType(order.getShippingType())
                .shippingFee(order.getShippingFee())
                .itemsSubtotal(order.getItemsSubtotal())
                .totalWeightKg(order.getTotalWeightKg())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .details(detailResponses)
                .items(detailResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
