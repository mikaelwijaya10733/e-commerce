package com.mikael.ecommerce.order.repository;

import com.mikael.ecommerce.order.entity.Order;
import com.mikael.ecommerce.order.entity.OrderStatus;
import com.mikael.ecommerce.order.repository.projection.BestSellerProjection;
import com.mikael.ecommerce.order.repository.projection.VipCustomerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCustomerEmailIgnoreCase(String customerEmail);

    @Query(value = """
            WITH ProductSales AS (
                SELECT oi.product_id AS productId,
                       oi.sku AS sku,
                       oi.product_name AS productName,
                       oi.category AS category,
                       SUM(oi.quantity) AS totalSold,
                       SUM(oi.subtotal) AS totalRevenue,
                       DENSE_RANK() OVER (PARTITION BY oi.category ORDER BY SUM(oi.quantity) DESC) AS categoryRank
                FROM order_items oi
                JOIN orders o ON oi.order_id = o.id
                WHERE o.status = 'COMPLETED'
                GROUP BY oi.product_id, oi.sku, oi.product_name, oi.category
            )
            SELECT productId, sku, productName, category, totalSold, totalRevenue, categoryRank
            FROM ProductSales
            WHERE categoryRank <= :topN
            ORDER BY category, totalSold DESC
            """, nativeQuery = true)
    List<BestSellerProjection> findBestSellerProductsPerCategory(@Param("topN") int topN);

    @Query(value = """
            SELECT o.customer_email AS customerEmail,
                   COUNT(o.id) AS totalCompletedOrders,
                   SUM(o.total_amount) AS totalSpent,
                   AVG(o.total_amount) AS averageOrderValue,
                   MAX(o.created_at) AS lastOrderDate
            FROM orders o
            WHERE o.status = 'COMPLETED'
            GROUP BY o.customer_email
            HAVING COUNT(o.id) >= :minOrders AND SUM(o.total_amount) >= :minSpend
            ORDER BY totalSpent DESC
            """, nativeQuery = true)
    List<VipCustomerProjection> findVipCustomers(
            @Param("minOrders") long minOrders,
            @Param("minSpend") BigDecimal minSpend);

    @Query(value = "SELECT * FROM orders WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
