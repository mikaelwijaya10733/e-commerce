package com.mikael.ecommerce.order.service;

import com.mikael.ecommerce.order.dto.OrderAnalyticsResponse;
import com.mikael.ecommerce.order.entity.Order;
import com.mikael.ecommerce.order.entity.OrderItem;
import com.mikael.ecommerce.order.entity.OrderStatus;
import com.mikael.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAnalyticsService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("1000000.00");
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderAnalyticsResponse calculateStreamAnalytics() {
        List<Order> allOrders = orderRepository.findAll();

        Map<OrderStatus, Long> ordersByStatusCount = allOrders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));

        Map<Boolean, List<String>> highValueOrderPartition = allOrders.stream()
                .collect(Collectors.partitioningBy(
                        order -> order.getTotalAmount().compareTo(HIGH_VALUE_THRESHOLD) >= 0,
                        Collectors.mapping(Order::getOrderNumber, Collectors.toList())
                ));

        DoubleSummaryStatistics amountStats = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .collect(Collectors.summarizingDouble(o -> o.getTotalAmount().doubleValue()));

        Map<String, Integer> quantitySoldByCategory = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getCategory,
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        List<String> topOrdersSortedByAmount = allOrders.stream()
                .sorted(Comparator.comparing(Order::getTotalAmount, Comparator.reverseOrder())
                        .thenComparing(Order::getCreatedAt, Comparator.reverseOrder()))
                .limit(5)
                .map(o -> String.format("%s (Rp %s, %s)", o.getOrderNumber(), o.getTotalAmount(), o.getStatus()))
                .collect(Collectors.toList());

        log.info("Calculated stream analytics across {} total orders.", allOrders.size());

        return OrderAnalyticsResponse.builder()
                .ordersByStatusCount(ordersByStatusCount)
                .highValueOrderPartition(highValueOrderPartition)
                .orderAmountStatistics(amountStats)
                .totalQuantitySoldByCategory(quantitySoldByCategory)
                .topOrdersSortedByAmount(topOrdersSortedByAmount)
                .build();
    }
}
