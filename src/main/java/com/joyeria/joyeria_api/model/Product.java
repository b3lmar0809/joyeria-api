package com.joyeria.joyeria_api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
/**
 * Product class
 *
 * @Version: 1.0.1 - 24 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 13 feb. 2026
 **/
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // BigDecimal es mejor que Double para dinero (evita errores de redondeo)
    @Column(nullable = false)
    private BigDecimal price;

    // cantidad disponible en inventario
    @Column(nullable = false)
    private Integer stock = 0;

    // SKU = Stock Keeping Unit (codigo unico del producto)
    // ej: "ANI-ORO18-DIA-001"
    @Column(unique = true)
    private String sku;

    // Peso en gramos
    private BigDecimal weight;

    private String dimensions;

    // FetchType.LAZY significa que no carga la categoría hasta que la pidas
    // (mejora el rendimiento)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    // @JsonIgnoreProperties evita problemas al convertir a JSON
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Material material;

    // piedras preciosas que tiene la joya
    private String gemstones;

    // @ElementCollection permite guardar una lista de valores simples
    // guardamos múltiples URLs de imagenes del producto
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();
    // ej: ["https://..../img1.jpg", "https://..../img2.jpg"]

    // para marcar productos destacados en la pagina principal
    private Boolean featured = false;

    // precio con descuento (si esta en oferta)
    // si es null, no hay descuento
    private BigDecimal discountPrice;

    // para activar/desactivar productos (soft delete)
    @Column(nullable = false)
    private Boolean active = true;

    // fecha de creacion del producto
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // fecha de ultima modificación
    private LocalDateTime updatedAt = LocalDateTime.now();

    //SE EJECUTA ANTES DE GUARDAR (INSERT)
    @PrePersist
    protected void onCreate() {
        if (this.active == null) {
            this.active = true;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    //SE EJECUTA ANTES DE ACTUALIZAR (UPDATE)
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}