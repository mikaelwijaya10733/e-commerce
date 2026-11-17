package com.mikael.ecommerce.order.controller;

import com.mikael.ecommerce.order.dto.ApiResponse;
import com.mikael.ecommerce.order.dto.CreateOrderRequest;
import com.mikael.ecommerce.order.dto.OrderAnalyticsResponse;
import com.mikael.ecommerce.order.dto.OrderResponse;
import com.mikael.ecommerce.order.repository.projection.BestSellerProjection;
import com.mikael.ecommerce.order.repository.projection.VipCustomerProjection;
import com.mikael.ecommerce.order.service.OrderAnalyticsService;
import com.mikael.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderAnalyticsService orderAnalyticsService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.checkoutOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order checkout completed successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Order found", order));
    }

    @GetMapping("/analytics/streams")
    public ResponseEntity<ApiResponse<OrderAnalyticsResponse>> getStreamAnalytics() {
        OrderAnalyticsResponse response = orderAnalyticsService.calculateStreamAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Stream analytics computed successfully", response));
    }

    @GetMapping("/analytics/best-sellers")
    public ResponseEntity<ApiResponse<List<BestSellerProjection>>> getBestSellers(
            @RequestParam(defaultValue = "3") int topN) {
        List<BestSellerProjection> bestSellers = orderService.getBestSellers(topN);
        return ResponseEntity.ok(ApiResponse.success("Best sellers per category (Native SQL Window Function)", bestSellers));
    }

    @GetMapping("/analytics/vip-customers")
    public ResponseEntity<ApiResponse<List<VipCustomerProjection>>> getVipCustomers(
            @RequestParam(defaultValue = "1") long minOrders,
            @RequestParam(defaultValue = "1000000") BigDecimal minSpend) {
        List<VipCustomerProjection> vips = orderService.getVipCustomers(minOrders, minSpend);
        return ResponseEntity.ok(ApiResponse.success("VIP customers (Native SQL Multi-table JOIN + HAVING)", vips));
    }
}
