package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.dto.CreateOrderRequest;
import com.joyeria.joyeria_api.dto.OrderItemRequest;
import com.joyeria.joyeria_api.exception.InsufficientStockException;
import com.joyeria.joyeria_api.exception.InvalidOperationException;
import com.joyeria.joyeria_api.exception.ResourceNotFoundException;
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

    public Order createOrder(CreateOrderRequest request) {
        // Validar que tenga items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOperationException("La orden debe tener al menos un producto");
        }

        // Crear la orden
        Order order = new Order();
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingCity(request.getShippingCity());
        order.setShippingState(request.getShippingState());
        order.setShippingPostalCode(request.getShippingPostalCode());
        order.setShippingCountry(request.getShippingCountry());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal subtotal = BigDecimal.ZERO;

        // procesar cada item
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productService.getProductById(itemReq.getProductId());

            // validar stock disponible
            if (product.getStock() < itemReq.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(),
                        product.getStock(),
                        itemReq.getQuantity()
                );
            }

            // crear OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());

            order.addItem(orderItem);

            // calcular subtotal
            subtotal = subtotal.add(orderItem.getSubtotal());
        }

        // establecer totales
        order.setSubtotal(subtotal);
        order.setShippingCost(BigDecimal.ZERO);
        order.setTax(BigDecimal.ZERO);

        BigDecimal total = subtotal
                .add(order.getShippingCost())
                .add(order.getTax());
        order.setTotalAmount(total);

        // guardar orden
        return orderRepository.save(order);
    }

    public  Order markOrderAsPaid(String paymentIntentId) {
        Order order = orderRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order", "paymentIntentId", paymentIntentId
                ));

        // si ya está pagada, no hacer nada
        if (order.getStatus() == OrderStatus.PAID) {
            return order;
        }

        // marcar como pagada
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        // reducir stock de los productos
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
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
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