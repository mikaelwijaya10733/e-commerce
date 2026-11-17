package com.mikael.ecommerce.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockOperationResponse {

    private Long productId;
    private String sku;
    private Integer remainingStock;
    private Integer affectedQuantity;
    private String message;
}
