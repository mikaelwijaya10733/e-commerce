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

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByCustomer_EmailIgnoreCase(String customerEmail);

    @Query(value = "SELECT nextval('order_number_seq')", nativeQuery = true)
    Long getNextOrderSequence();

    @Query(value = """
            WITH ProductSales AS (
                SELECT od.product_id AS productId,
                       od.sku AS sku,
                       od.product_name AS productName,
                       od.category AS category,
                       SUM(od.quantity) AS totalSold,
                       SUM(od.subtotal) AS totalRevenue,
                       DENSE_RANK() OVER (PARTITION BY od.category ORDER BY SUM(od.quantity) DESC) AS categoryRank
                FROM order_details od
                JOIN orders o ON od.order_id = o.id
                WHERE o.status = 'COMPLETED'
                GROUP BY od.product_id, od.sku, od.product_name, od.category
            )
            SELECT productId, sku, productName, category, totalSold, totalRevenue, categoryRank
            FROM ProductSales
            WHERE categoryRank <= :topN
            ORDER BY category, totalSold DESC
            """, nativeQuery = true)
    List<BestSellerProjection> findBestSellerProductsPerCategory(@Param("topN") int topN);

    @Query(value = """
            SELECT c.id AS customerId,
                   c.name AS customerName,
                   c.email AS customerEmail,
                   COUNT(o.id) AS totalCompletedOrders,
                   SUM(o.total_amount) AS totalSpent,
                   AVG(o.total_amount) AS averageOrderValue,
                   MAX(o.created_at) AS lastOrderDate
            FROM customers c
            JOIN orders o ON o.customer_id = c.id
            WHERE o.status = 'COMPLETED'
            GROUP BY c.id, c.name, c.email
            HAVING COUNT(o.id) >= :minOrders AND SUM(o.total_amount) >= :minSpend
            ORDER BY totalSpent DESC
            """, nativeQuery = true)
    List<VipCustomerProjection> findVipCustomers(
            @Param("minOrders") long minOrders,
            @Param("minSpend") BigDecimal minSpend);

    @Query(value = "SELECT * FROM orders WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
