package com.mikael.ecommerce.product.service;

import com.mikael.ecommerce.product.dto.StockOperationResponse;
import com.mikael.ecommerce.product.entity.Product;
import com.mikael.ecommerce.product.exception.InsufficientStockException;
import com.mikael.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Concurrency and Stock Logic Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should successfully reserve stock when sufficient quantity is available")
    void testReserveStockSuccess() {
        Product product = Product.builder()
                .id(1L)
                .sku("TEST-SKU-001")
                .name("Test Product")
                .stockQuantity(10)
                .price(new BigDecimal("100000.00"))
                .weightKg(new BigDecimal("1.0"))
                .build();

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        StockOperationResponse response = productService.reserveStock(1L, 3);

        assertNotNull(response);
        assertEquals(7, response.getRemainingStock());
        assertEquals(3, response.getAffectedQuantity());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when requested quantity exceeds stock")
    void testReserveStockInsufficient() {
        Product product = Product.builder()
                .id(1L)
                .sku("TEST-SKU-001")
                .name("Test Product")
                .stockQuantity(2)
                .build();

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> productService.reserveStock(1L, 5));
        verify(productRepository, never()).save(any(Product.class));
    }
}
