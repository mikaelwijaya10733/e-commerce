package com.mikael.ecommerce.order.service;

import com.mikael.ecommerce.order.dto.OrderAnalyticsResponse;
import com.mikael.ecommerce.order.entity.Order;
import com.mikael.ecommerce.order.entity.OrderItem;
import com.mikael.ecommerce.order.entity.OrderStatus;
import com.mikael.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Java Stream Operations Unit Tests")
class OrderAnalyticsStreamTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderAnalyticsService analyticsService;

    @Test
    @DisplayName("Should successfully compute grouping, partitioning, and statistical streams")
    void testStreamAnalyticsComputation() {
        Order order1 = Order.builder()
                .orderNumber("ORD-101")
                .status(OrderStatus.COMPLETED)
                .totalAmount(new BigDecimal("1500000.00"))
                .build();
        order1.addItem(OrderItem.builder()
                .category("ELECTRONICS")
                .quantity(2)
                .build());

        Order order2 = Order.builder()
                .orderNumber("ORD-102")
                .status(OrderStatus.COMPLETED)
                .totalAmount(new BigDecimal("350000.00"))
                .build();
        order2.addItem(OrderItem.builder()
                .category("BOOKS")
                .quantity(1)
                .build());

        Order order3 = Order.builder()
                .orderNumber("ORD-103")
                .status(OrderStatus.CANCELLED)
                .totalAmount(new BigDecimal("500000.00"))
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3));

        OrderAnalyticsResponse response = analyticsService.calculateStreamAnalytics();

        assertNotNull(response);
        assertEquals(2L, response.getOrdersByStatusCount().get(OrderStatus.COMPLETED));
        assertEquals(1L, response.getOrdersByStatusCount().get(OrderStatus.CANCELLED));
        assertTrue(response.getHighValueOrderPartition().get(true).contains("ORD-101"));
        assertTrue(response.getHighValueOrderPartition().get(false).contains("ORD-102"));
        assertEquals(2, response.getOrderAmountStatistics().getCount());
        assertEquals(1850000.00, response.getOrderAmountStatistics().getSum(), 0.01);
        assertEquals(2, response.getTotalQuantitySoldByCategory().get("ELECTRONICS"));
        assertEquals(1, response.getTotalQuantitySoldByCategory().get("BOOKS"));
    }
}
