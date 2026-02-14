package com.joyeria.joyeria_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"items", "user"})// Evita ciclos al serializa
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // IMPORTANTE: guardamos el precio al momento de la compra
    // Porque el precio del producto puede cambiar despues
    // pero el cliente pago el precio que estaba en ese momento
    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    // Guardamos tambien el nombre y SKU por si el producto se elimina
    private String productName;
    private String productSku;

    //Metodo para calcular el subtotal de este item
    // Subtotal = precio × cantidad
    public BigDecimal getSubtotal() {
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}