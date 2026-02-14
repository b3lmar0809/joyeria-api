package com.joyeria.joyeria_api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relacion con el usuario (si está registrado)
    // puede ser null si compra como invitado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(nullable = false)
    private String customerEmail;

    private String customerName;
    private String customerPhone;

    @Column(nullable = false)
    private BigDecimal subtotal;

    private BigDecimal shippingCost = BigDecimal.ZERO;

    // impuestos
    private BigDecimal tax = BigDecimal.ZERO;

    // total final = subtotal + shippingCost + tax
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // estado actual de la orden (PENDING, PAID, SHIPPED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    // ID del pago en Stripe (para rastrear el pago)
    private String stripePaymentIntentId;

    // cascade = CascadeType.ALL: si borras la orden, borra los items
    // orphanRemoval = true: si quitas un item de la lista, lo borra de la BD
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String shippingCity;

    @Column(nullable = false)
    private String shippingState;

    @Column(nullable = false)
    private String shippingPostalCode;

    @Column(nullable = false)
    private String shippingCountry;

    // notas especiales del cliente
    // ej: "Envío de regalo, no incluir factura"
    @Column(columnDefinition = "TEXT")
    private String notes;

    // numero de rastreo de la paqueteria (FedEx, DHL, etc.)
    private String trackingNumber;

    // fechas
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt; // fecha de pago
    private LocalDateTime shippedAt; // fecha de envio
    private LocalDateTime deliveredAt; //fecha de entrega

    // agrega un item a la orden y establece la relacion bidireccional
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this); // establece la relacion inversa
    }

    // remueve un item de la orden
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null); // limpia la relacion
    }
}