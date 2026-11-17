package com.mikael.ecommerce.order.client;

import com.mikael.ecommerce.order.dto.ApiResponse;
import com.mikael.ecommerce.order.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {

    private final RestClient productRestClient;

    public ProductDto getProductById(Long productId) {
        log.info("Calling product-service to fetch product id: {}", productId);
        try {
            ApiResponse<ProductDto> response = productRestClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("Failed to fetch product from product-service. Status: " + res.getStatusCode());
                    })
                    .body(new ParameterizedTypeReference<ApiResponse<ProductDto>>() {});

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Product not found or empty response from product-service for id: " + productId);
            }
            return response.getData();
        } catch (Exception e) {
            log.error("Error communicating with product-service for productId {}: {}", productId, e.getMessage());
            throw new RuntimeException("Inter-service communication error: " + e.getMessage(), e);
        }
    }

    public void reserveStock(Long productId, Integer quantity) {
        log.info("Calling product-service to reserve stock: productId={}, qty={}", productId, quantity);
        try {
            productRestClient.post()
                    .uri("/api/products/{id}/reserve-stock", productId)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("Failed to reserve stock. Status: " + res.getStatusCode());
                    })
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Stock reservation failed for productId {}: {}", productId, e.getMessage());
            throw new RuntimeException("Cannot reserve stock: " + e.getMessage(), e);
        }
    }

    public void releaseStock(Long productId, Integer quantity) {
        log.info("Calling product-service to rollback/release stock: productId={}, qty={}", productId, quantity);
        try {
            productRestClient.post()
                    .uri("/api/products/{id}/release-stock", productId)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Stock rollback failed for productId {}: {}", productId, e.getMessage());
        }
    }
}
