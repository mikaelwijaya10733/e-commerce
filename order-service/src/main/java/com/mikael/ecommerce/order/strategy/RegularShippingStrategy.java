package com.mikael.ecommerce.order.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("REGULAR")
public class RegularShippingStrategy implements ShippingStrategy {

    private static final BigDecimal RATE_PER_KG = new BigDecimal("10000.00");
    private static final BigDecimal MIN_FEE = new BigDecimal("10000.00");

    @Override
    public String getStrategyCode() {
        return "REGULAR";
    }

    @Override
    public BigDecimal calculateFee(BigDecimal totalWeightKg, BigDecimal orderAmount) {
        BigDecimal billableWeight = totalWeightKg.max(BigDecimal.ONE);
        BigDecimal fee = billableWeight.multiply(RATE_PER_KG);
        return fee.max(MIN_FEE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return "Regular Ground Delivery (2-3 business days)";
    }
}
