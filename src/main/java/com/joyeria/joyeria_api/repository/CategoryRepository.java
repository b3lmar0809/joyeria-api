package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    // verificar si existe una categoria con ese nombre
    // SQL generado: SELECT COUNT(*) > 0 FROM categories WHERE name = ?
    Optional<Category> findByNameIgnoreCase(String name);

    Boolean existsByName(String name);

    java.util.List<Category> findByActiveTrue();

    // traer categorias activas ordenadas por nombre
    // SQL generado: SELECT * FROM categories WHERE active = true ORDER BY name ASC
    java.util.List<Category> findByActiveTrueOrderByNameAsc();

}
