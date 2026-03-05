package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.dto.CreateOrderRequest;
import com.joyeria.joyeria_api.model.Order;
import com.joyeria.joyeria_api.model.OrderStatus;
import com.joyeria.joyeria_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/**
 * OrderController class
 *
 * @Version: 1.0.1- 02 mar. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 - 14 feb. 2026
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    //obtener las ultimas 10 ordenes
    @GetMapping("/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> orders = orderService.getRecentOrders();
        return ResponseEntity.ok(orders);
    }

    //calcular ventas totales en un periodo
    @GetMapping("/sales")
    public ResponseEntity<BigDecimal> calculateSales(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        BigDecimal total = orderService.calculateTotalSales(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Order> partialUpdateOrder(
            @PathVariable Long id,
            @RequestBody Order order
    ) {
        Order updated = orderService.partialUpdateOrder(id, order);
        return ResponseEntity.ok(updated);
    }

    // Cambiar el estado de la orden Body: { "status": "SHIPPED" }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request
    ) {
        Order updated = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/tracking")
    public ResponseEntity<Order> updateTrackingNumber(
            @PathVariable Long id,
            @RequestBody TrackingUpdateRequest request
    ) {
        Order updated = orderService.updateTrackingNumber(id, request.getTrackingNumber());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order cancelled = orderService.cancelOrder(id);
        return ResponseEntity.ok(cancelled);
    }

    //DTOs internos

    private static class StatusUpdateRequest {
        private OrderStatus status;

        public OrderStatus getStatus() {
            return status;
        }

        public void setStatus(OrderStatus status) {
            this.status = status;
        }
    }

    private static class TrackingUpdateRequest {
        private String trackingNumber;

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
    }
    // ========== ENDPOINTS CON PAGINACIÓN ==========

    //obtioen todas las orden con paginacion para el admin
    @GetMapping("/all")
    public ResponseEntity<Page<Order>> getAllOrdersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<Order> orders = orderService.getAllOrdersPaginated(pageable);

        return ResponseEntity.ok(orders);
    }
    //obtiene las ordenes por estado de paginacion
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Order>> getOrdersByStatusPaginated(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderService.getOrdersByStatusPaginated(status, pageable);

        return ResponseEntity.ok(orders);
    }

   //obtiene las ordenes por paginacion
    @GetMapping("/customer/{email}")
    public ResponseEntity<Page<Order>> getOrdersByCustomerPaginated(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderService.getOrdersByCustomerPaginated(email, pageable);

        return ResponseEntity.ok(orders);
    }
}