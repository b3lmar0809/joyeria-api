package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.Order;
import com.joyeria.joyeria_api.model.OrderItem;
import com.joyeria.joyeria_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByProduct(Product product);

    // cuantas veces se ha vendido un producto
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE " +
            "oi.product.id = :productId AND " +
            "oi.order.status = 'PAID'")
    Long countSoldQuantityByProduct(@Param("productId") Long productId);

    // productos mas vendidos
    // retorna una lista de Object[] donde:
    // [0] = Product, [1] = cantidad total vendida
    @Query("SELECT oi.product, SUM(oi.quantity) as total FROM OrderItem oi " +
            "WHERE oi.order.status = 'PAID' " +
            "GROUP BY oi.product " +
            "ORDER BY total DESC")
    List<Object[]> findBestSellingProducts();

    // Calcular ingresos generados por un producto
    @Query("SELECT SUM(oi.priceAtPurchase * oi.quantity) FROM OrderItem oi " +
            "WHERE oi.product.id = :productId AND oi.order.status = 'PAID'")
    BigDecimal calculateRevenueByProduct(@Param("productId") Long productId);
}