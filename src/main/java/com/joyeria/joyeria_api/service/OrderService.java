package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.model.Order;
import com.joyeria.joyeria_api.model.OrderItem;
import com.joyeria.joyeria_api.model.OrderStatus;
import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public Order createOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("La orden debe tener al menos un producto");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            Product product = productService.getProductById(item.getProduct().getId());

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                        "Stock insuficiente para: " + product.getName() +
                                ". Disponible: " + product.getStock()
                );
            }

            item.setPriceAtPurchase(product.getPrice());
            item.setProductName(product.getName());
            item.setProductSku(product.getSku());

            BigDecimal itemSubtotal = item.getSubtotal();
            subtotal = subtotal.add(itemSubtotal);

            item.setOrder(order);
        }

        order.setSubtotal(subtotal);

        BigDecimal total = subtotal
                .add(order.getShippingCost())
                .add(order.getTax());
        order.setTotalAmount(total);

        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    public Order markOrderAsPaid(String paymentIntentId) {
        Order order = orderRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException(
                        "Orden no encontrada para PaymentIntent: " + paymentIntentId
                ));

        if (order.getStatus() == OrderStatus.PAID) {
            return order;
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        for (OrderItem item : order.getItems()) {
            productService.reduceStock(
                    item.getProduct().getId(),
                    item.getQuantity()
            );
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Order partialUpdateOrder(Long id, Order orderDetails) {
        Order order = getOrderById(id);

        // Actualizar solo los campos que vienen
        if (orderDetails.getStatus() != null) {
            order.setStatus(orderDetails.getStatus());
        }
        if (orderDetails.getTrackingNumber() != null) {
            order.setTrackingNumber(orderDetails.getTrackingNumber());
        }
        if (orderDetails.getNotes() != null) {
            order.setNotes(orderDetails.getNotes());
        }

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = getOrderById(id);

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        switch (newStatus) {
            case PAID:
                if (order.getPaidAt() == null) {
                    order.setPaidAt(LocalDateTime.now());
                }
                break;
            case SHIPPED:
                if (order.getShippedAt() == null) {
                    order.setShippedAt(LocalDateTime.now());
                }
                break;
            case DELIVERED:
                if (order.getDeliveredAt() == null) {
                    order.setDeliveredAt(LocalDateTime.now());
                }
                break;
            case CANCELLED:
                if (oldStatus == OrderStatus.PAID || oldStatus == OrderStatus.PROCESSING) {
                    returnStockToInventory(order);
                }
                break;
            case REFUNDED:
                returnStockToInventory(order);
                break;
        }

        return orderRepository.save(order);
    }

    public Order updateTrackingNumber(Long id, String trackingNumber) {
        Order order = getOrderById(id);
        order.setTrackingNumber(trackingNumber);

        if (order.getStatus() != OrderStatus.SHIPPED &&
                order.getStatus() != OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.SHIPPED);
            order.setShippedAt(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    private void returnStockToInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            productService.increaseStock(
                    item.getProduct().getId(),
                    item.getQuantity()
            );
        }
    }

    public Order cancelOrder(Long id) {
        return updateOrderStatus(id, OrderStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSales(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = orderRepository.calculateTotalSales(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<Order> getRecentOrders() {
        return orderRepository.findRecentOrders(10);
    }
}