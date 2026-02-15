package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.Category;
import com.joyeria.joyeria_api.model.Material;
import com.joyeria.joyeria_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Boolean existsBySku(String sku);

    List<Product> findByActiveTrue();

    // traer productos activos ordenados por fecha (mas recientes primero)
    List<Product> findByActiveTrueOrderByCreatedAtDesc();

    // traer productos por categoría
    java.util.List<Product> findByCategoryAndActiveTrue(Category category);

    java.util.List<Product> findByMaterialAndActiveTrue(Material material);

    // traer productos destacados (featured)
    List<Product> findByFeaturedTrueAndActiveTrue();

    // buscar productos por nombre (que contenga el texto)
    // SQL: SELECT * FROM products WHERE name LIKE %?% AND active = true
    // ej: findByNameContainingIgnoreCaseAndActiveTrue("anillo")
    // Encontrara: anillo de oro, anillo con diamante
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    // buscar productos en un rango de precio
    List<Product> findByPriceBetweenAndActiveTrue(
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    // traer productos con stock disponible
    List<Product> findByStockGreaterThanAndActiveTrue(Integer stock);

    // buscar productos por multiples criterios
    // JPQL es como SQL pero usa nombres de clases y atributos en vez de tablas
    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:materialId IS NULL OR p.material.id = :materialId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "p.active = true")
    List<Product> findByFilters(
            @Param("categoryId") Long categoryId,
            @Param("materialId") Long materialId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
    // @Param conecta los parametros del metodo con los de la consulta

    // buscar productos en oferta (tienen descuento)
    @Query("SELECT p FROM Product p WHERE p.discountPrice IS NOT NULL AND p.active = true")
    List<Product> findProductsOnSale();

    // contar productos por categoría
    // para mostrar "Anillos (25)" en el filtro
    Long countByCategoryAndActiveTrue(Category category);
}
