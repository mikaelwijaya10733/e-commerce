package com.mikael.ecommerce.order.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingFeeCalculator {

    private final Map<String, ShippingStrategy> strategies;

    public BigDecimal calculate(String shippingType, BigDecimal totalWeightKg, BigDecimal orderAmount) {
        String key = (shippingType == null) ? "REGULAR" : shippingType.trim().toUpperCase();
        ShippingStrategy strategy = strategies.get(key);

        if (strategy == null) {
            log.warn("Unknown shipping type '{}'. Falling back to REGULAR strategy.", shippingType);
            strategy = strategies.get("REGULAR");
        }

        BigDecimal fee = strategy.calculateFee(totalWeightKg, orderAmount);
        log.info("Calculated shipping fee via strategy [{}]: {} IDR for weight {} kg",
                strategy.getStrategyCode(), fee, totalWeightKg);
        return fee;
    }

    public Map<String, ShippingStrategy> getAvailableStrategies() {
        return strategies;
    }
}
