package com.mikael.ecommerce.order.dto;

import com.mikael.ecommerce.order.entity.OrderDetail;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private String category;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal unitWeightKg;

    public static OrderDetailResponse fromEntity(OrderDetail detail) {
        if (detail == null) {
            return null;
        }
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProductId())
                .sku(detail.getSku())
                .productName(detail.getProductName())
                .category(detail.getCategory())
                .unitPrice(detail.getUnitPrice())
                .quantity(detail.getQuantity())
                .subtotal(detail.getSubtotal())
                .unitWeightKg(detail.getUnitWeightKg())
                .build();
    }
}
