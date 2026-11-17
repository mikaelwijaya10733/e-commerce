package com.mikael.ecommerce.order.strategy;

import java.math.BigDecimal;

public interface ShippingStrategy {

    String getStrategyCode();

    BigDecimal calculateFee(BigDecimal totalWeightKg, BigDecimal orderAmount);

    String getDescription();
}
