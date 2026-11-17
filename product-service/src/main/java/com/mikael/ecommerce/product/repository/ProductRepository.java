package com.mikael.ecommerce.product.repository;

import com.mikael.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryIgnoreCase(String category);

    @Query(value = "SELECT * FROM products WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Query(value = """
            WITH RankedProducts AS (
                SELECT p.*,
                       DENSE_RANK() OVER (PARTITION BY p.category ORDER BY p.price DESC) AS price_rank
                FROM products p
            )
            SELECT id, sku, name, description, category, price, stock_quantity, weight_kg, created_at, updated_at
            FROM RankedProducts
            WHERE price_rank <= :topRank
            ORDER BY category, price DESC
            """, nativeQuery = true)
    List<Product> findTopRankedProductsPerCategory(@Param("topRank") int topRank);
}
