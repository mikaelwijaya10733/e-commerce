package com.mikael.ecommerce.order.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("CARGO")
public class CargoShippingStrategy implements ShippingStrategy {

    private static final BigDecimal RATE_PER_KG = new BigDecimal("6000.00");
    private static final BigDecimal MIN_FEE = new BigDecimal("60000.00");

    @Override
    public String getStrategyCode() {
        return "CARGO";
    }

    @Override
    public BigDecimal calculateFee(BigDecimal totalWeightKg, BigDecimal orderAmount) {
        BigDecimal billableWeight = totalWeightKg.max(new BigDecimal("10.00"));
        BigDecimal fee = billableWeight.multiply(RATE_PER_KG);
        return fee.max(MIN_FEE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescription() {
        return "Heavy Cargo / Bulk Freight Delivery (3-5 business days)";
    }
}
