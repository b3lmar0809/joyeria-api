package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.Order;
import com.joyeria.joyeria_api.model.OrderStatus;
import com.joyeria.joyeria_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByCustomerEmail(String email);

    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String email);

    List<Order> findByStatus(OrderStatus status);

    // buscar orden por el PaymentIntent de Stripe
    // esto es muy importante para los webhooks de Stripe
    Optional<Order> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<Order> findByUserAndStatus(User user, OrderStatus status);

    // buscar ordenes creadas en un rango de fechas
    // pa reportes de ventas
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar ordenes pagadas en un rango de fecha
    List<Order> findByPaidAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // calcula el total de ventas en un período
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE " +
            "o.status = 'PAID' AND " +
            "o.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    // retorna la suma de todos los totalAmount de ordenes pagadas

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);

    // nativeQuery = true permite usar SQL directo (no JPQL)
    @Query(value = "SELECT * FROM orders ORDER BY created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<Order> findRecentOrders(@Param("limit") int limit);

    // buscar ordenes pendientes de hace mas de X horas
    // (para cancelar automaticamente o enviar recordatorio)
    @Query("SELECT o FROM Order o WHERE " +
            "o.status = 'PENDING' AND " +
            "o.createdAt < :cutoffTime")
    List<Order> findPendingOrdersOlderThan(
            @Param("cutoffTime") LocalDateTime cutoffTime
    );

}
