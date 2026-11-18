package com.mikael.ecommerce.order.dto;

import com.mikael.ecommerce.order.entity.OrderDetail;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private String category;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal unitWeightKg;

    public static OrderItemResponse fromEntity(OrderDetail item) {
        if (item == null) {
            return null;
        }
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .sku(item.getSku())
                .productName(item.getProductName())
                .category(item.getCategory())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .unitWeightKg(item.getUnitWeightKg())
                .build();
    }
}
