package com.mikael.ecommerce.order.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("EXPRESS")
public class ExpressShippingStrategy implements ShippingStrategy {

    private static final BigDecimal RATE_PER_KG = new BigDecimal("25000.00");
    private static final BigDecimal MIN_FEE = new BigDecimal("30000.00");

    @Override
    public String getStrategyCode() {
        return "EXPRESS";
    }

    @Override
    public BigDecimal calculateFee(BigDecimal totalWeightKg, BigDecimal orderAmount) {
        BigDecimal billableWeight = totalWeightKg.max(BigDecimal.ONE);
        BigDecimal fee = billableWeight.multiply(RATE_PER_KG);
        return fee.max(MIN_FEE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return "Next-Day Priority Express Delivery";
    }
}
