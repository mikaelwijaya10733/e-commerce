package com.mikael.ecommerce.order.repository.projection;

import java.math.BigDecimal;

public interface BestSellerProjection {
    Long getProductId();
    String getSku();
    String getProductName();
    String getCategory();
    Long getTotalSold();
    BigDecimal getTotalRevenue();
    Integer getCategoryRank();
}
