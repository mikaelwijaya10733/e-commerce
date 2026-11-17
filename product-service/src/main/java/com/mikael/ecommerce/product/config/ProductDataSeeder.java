package com.mikael.ecommerce.product.config;

import com.mikael.ecommerce.product.entity.Product;
import com.mikael.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Products already seeded.");
            return;
        }

        List<Product> seedProducts = List.of(
                Product.builder()
                        .sku("ELEC-LPTP-001")
                        .name("Laptop Ultra 14-inch")
                        .description("High-performance laptop 16GB RAM 512GB SSD")
                        .category("ELECTRONICS")
                        .price(new BigDecimal("12500000.00"))
                        .stockQuantity(15)
                        .weightKg(new BigDecimal("1.45"))
                        .build(),
                Product.builder()
                        .sku("ELEC-SMFN-002")
                        .name("Smartphone Pro Max")
                        .description("Flagship smartphone with 120Hz AMOLED")
                        .category("ELECTRONICS")
                        .price(new BigDecimal("8900000.00"))
                        .stockQuantity(30)
                        .weightKg(new BigDecimal("0.22"))
                        .build(),
                Product.builder()
                        .sku("ELEC-HEAD-003")
                        .name("Noise-Cancelling Headphones")
                        .description("Wireless Bluetooth headphones with ANC")
                        .category("ELECTRONICS")
                        .price(new BigDecimal("1850000.00"))
                        .stockQuantity(40)
                        .weightKg(new BigDecimal("0.35"))
                        .build(),
                Product.builder()
                        .sku("FASH-JACK-001")
                        .name("Waterproof Windbreaker Jacket")
                        .description("Lightweight all-weather outdoor jacket")
                        .category("FASHION")
                        .price(new BigDecimal("450000.00"))
                        .stockQuantity(60)
                        .weightKg(new BigDecimal("0.40"))
                        .build(),
                Product.builder()
                        .sku("FASH-SHOE-002")
                        .name("Ergonomic Running Shoes")
                        .description("Breathable running sneakers with cushion sole")
                        .category("FASHION")
                        .price(new BigDecimal("750000.00"))
                        .stockQuantity(25)
                        .weightKg(new BigDecimal("0.85"))
                        .build(),
                Product.builder()
                        .sku("BOOK-JAVA-001")
                        .name("Mastering Spring Boot & Microservices")
                        .description("Complete architectural guide to cloud-native Java")
                        .category("BOOKS")
                        .price(new BigDecimal("280000.00"))
                        .stockQuantity(50)
                        .weightKg(new BigDecimal("0.65"))
                        .build(),
                Product.builder()
                        .sku("BOOK-ARCH-002")
                        .name("Designing Data-Intensive Applications")
                        .description("Reliable, scalable, and maintainable systems")
                        .category("BOOKS")
                        .price(new BigDecimal("350000.00"))
                        .stockQuantity(35)
                        .weightKg(new BigDecimal("0.75"))
                        .build(),
                Product.builder()
                        .sku("FURN-CHAIR-001")
                        .name("Ergonomic Mesh Office Chair")
                        .description("Lumbar support office desk chair with armrests")
                        .category("FURNITURE")
                        .price(new BigDecimal("1950000.00"))
                        .stockQuantity(12)
                        .weightKg(new BigDecimal("14.50"))
                        .build()
        );

        productRepository.saveAll(seedProducts);
        log.info("Successfully seeded {} products into product-service database.", seedProducts.size());
    }
}
