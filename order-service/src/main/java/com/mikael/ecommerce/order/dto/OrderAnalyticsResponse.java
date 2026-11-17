package com.mikael.ecommerce.order.dto;

import com.mikael.ecommerce.order.entity.OrderStatus;
import lombok.*;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalyticsResponse {

    private Map<OrderStatus, Long> ordersByStatusCount;
    private Map<Boolean, List<String>> highValueOrderPartition;
    private DoubleSummaryStatistics orderAmountStatistics;
    private Map<String, Integer> totalQuantitySoldByCategory;
    private List<String> topOrdersSortedByAmount;
}
