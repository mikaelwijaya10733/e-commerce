package com.mikael.ecommerce.order.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VipCustomerProjection {
    Long getCustomerId();
    String getCustomerName();
    String getCustomerEmail();
    Long getTotalCompletedOrders();
    BigDecimal getTotalSpent();
    BigDecimal getAverageOrderValue();
    LocalDateTime getLastOrderDate();
}
