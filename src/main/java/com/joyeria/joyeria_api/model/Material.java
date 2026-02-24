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
 * Material class
 *
 * @Version: 1.0.1 - 24 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 13 feb. 2026
 */
@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // para activar/desactivar materiales
    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "material")
    @JsonBackReference
    private List<Product> products;

    public Material(Long id) {
        this.id = id;
    }

    @PrePersist
    public void prePersist() {
        if (this.active == null) {
            this.active = true;
        }
    }
}