package com.mikael.ecommerce.product.service;

import com.mikael.ecommerce.product.dto.CreateProductRequest;
import com.mikael.ecommerce.product.dto.ProductResponse;
import com.mikael.ecommerce.product.dto.StockOperationResponse;
import com.mikael.ecommerce.product.entity.Product;
import com.mikael.ecommerce.product.exception.InsufficientStockException;
import com.mikael.ecommerce.product.exception.ResourceNotFoundException;
import com.mikael.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(String category) {
        List<Product> products = (category != null && !category.isBlank())
                ? productRepository.findByCategoryIgnoreCase(category)
                : productRepository.findAll();

        return products.stream()
                .map(ProductResponse::fromEntity)
                .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory().toUpperCase())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .weightKg(request.getWeightKg())
                .build();

        Product saved = productRepository.save(product);
        log.info("Created product sku: {}, id: {}", saved.getSku(), saved.getId());
        return ProductResponse.fromEntity(saved);
    }

    @Transactional
    public StockOperationResponse reserveStock(Long productId, Integer quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock for product '%s'. Requested: %d, Available: %d",
                    product.getName(), quantity, product.getStockQuantity()));
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        log.info("Stock reserved for product {}: deducted {}, remaining {}",
                productId, quantity, product.getStockQuantity());

        return StockOperationResponse.builder()
                .productId(product.getId())
                .sku(product.getSku())
                .affectedQuantity(quantity)
                .remainingStock(product.getStockQuantity())
                .message("Stock successfully reserved")
                .build();
    }

    @Transactional
    public StockOperationResponse releaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        log.info("Stock released for product {}: added back {}, new stock {}",
                productId, quantity, product.getStockQuantity());

        return StockOperationResponse.builder()
                .productId(product.getId())
                .sku(product.getSku())
                .affectedQuantity(quantity)
                .remainingStock(product.getStockQuantity())
                .message("Stock successfully released")
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getTopRankedProductsPerCategory(int topRank) {
        return productRepository.findTopRankedProductsPerCategory(topRank).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getProductCountByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
    }
}
