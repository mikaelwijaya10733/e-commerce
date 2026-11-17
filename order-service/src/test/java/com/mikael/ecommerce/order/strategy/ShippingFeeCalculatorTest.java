package com.mikael.ecommerce.order.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Spring IoC Shipping Strategy Unit Tests")
class ShippingFeeCalculatorTest {

    private ShippingFeeCalculator calculator;

    @BeforeEach
    void setUp() {
        Map<String, ShippingStrategy> strategies = Map.of(
                "REGULAR", new RegularShippingStrategy(),
                "EXPRESS", new ExpressShippingStrategy(),
                "CARGO", new CargoShippingStrategy()
        );
        calculator = new ShippingFeeCalculator(strategies);
    }

    @Test
    @DisplayName("Should correctly calculate regular shipping fee")
    void testRegularShipping() {
        BigDecimal fee = calculator.calculate("REGULAR", new BigDecimal("2.5"), new BigDecimal("200000.00"));
        assertEquals(new BigDecimal("25000.00"), fee);
    }

    @Test
    @DisplayName("Should correctly calculate express shipping fee")
    void testExpressShipping() {
        BigDecimal fee = calculator.calculate("EXPRESS", new BigDecimal("1.2"), new BigDecimal("500000.00"));
        assertEquals(new BigDecimal("30000.00"), fee);
    }

    @Test
    @DisplayName("Should apply cargo discounted freight rate for heavy orders")
    void testCargoShipping() {
        BigDecimal fee = calculator.calculate("CARGO", new BigDecimal("15.0"), new BigDecimal("1500000.00"));
        assertEquals(new BigDecimal("90000.00"), fee);
    }

    @Test
    @DisplayName("Should fallback to regular shipping strategy on unknown type")
    void testFallbackShipping() {
        BigDecimal fee = calculator.calculate("UNKNOWN_TYPE", new BigDecimal("1.0"), new BigDecimal("100000.00"));
        assertEquals(new BigDecimal("10000.00"), fee);
    }
}
