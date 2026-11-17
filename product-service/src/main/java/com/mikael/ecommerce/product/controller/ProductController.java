package com.mikael.ecommerce.product.controller;

import com.mikael.ecommerce.product.dto.ApiResponse;
import com.mikael.ecommerce.product.dto.CreateProductRequest;
import com.mikael.ecommerce.product.dto.ProductResponse;
import com.mikael.ecommerce.product.dto.ReserveStockRequest;
import com.mikael.ecommerce.product.dto.StockOperationResponse;
import com.mikael.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String category) {
        List<ProductResponse> products = productService.getAllProducts(category);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product found", product));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", created));
    }

    @PostMapping("/{id}/reserve-stock")
    public ResponseEntity<ApiResponse<StockOperationResponse>> reserveStock(
            @PathVariable Long id,
            @Valid @RequestBody ReserveStockRequest request) {
        StockOperationResponse response = productService.reserveStock(id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Stock reserved successfully", response));
    }

    @PostMapping("/{id}/release-stock")
    public ResponseEntity<ApiResponse<StockOperationResponse>> releaseStock(
            @PathVariable Long id,
            @Valid @RequestBody ReserveStockRequest request) {
        StockOperationResponse response = productService.releaseStock(id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Stock released successfully", response));
    }

    @GetMapping("/analytics/top-ranked")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getTopRankedProducts(
            @RequestParam(defaultValue = "3") int topRank) {
        List<ProductResponse> products = productService.getTopRankedProductsPerCategory(topRank);
        return ResponseEntity.ok(ApiResponse.success("Top ranked products per category", products));
    }

    @GetMapping("/analytics/category-counts")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCategoryCounts() {
        Map<String, Long> counts = productService.getProductCountByCategory();
        return ResponseEntity.ok(ApiResponse.success("Product category distribution", counts));
    }
}
