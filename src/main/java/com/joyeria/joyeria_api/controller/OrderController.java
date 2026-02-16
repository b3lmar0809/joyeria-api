package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.model.Order;
import com.joyeria.joyeria_api.model.OrderStatus;
import com.joyeria.joyeria_api.service.OrderService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Order>> getOrdersByEmail(@PathVariable String email) {
        List<Order> orders = orderService.getOrdersByEmail(email);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
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
}