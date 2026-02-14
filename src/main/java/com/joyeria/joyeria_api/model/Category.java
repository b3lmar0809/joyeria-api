package com.joyeria.joyeria_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}