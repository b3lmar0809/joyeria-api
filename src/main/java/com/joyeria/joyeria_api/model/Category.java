package com.joyeria.joyeria_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Category class
 *
 * @Version: 1.0.1 - 24 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 13 feb. 2026
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // columnDefinition = "TEXT" permite textos largos (mas de 255 caracteres)
    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    // Para "eliminar" categorias sin borrarlas de la BD (soft delete)
    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Product> products;

    public Category(Long id) {
        this.id = id;
    }
    //Se ejecuta ANTES de guardar en la BD
    //Si active es null, lo establece en true
    //Garantiza que siempre tenga un valor
    @PrePersist
    public void prePersist() {
        if (this.active == null) {
            this.active = true;
        }
    }
}