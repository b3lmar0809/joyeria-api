package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.model.Review;
import com.joyeria.joyeria_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // buscar todas las reseñas de un producto (solo aprobadas)
    List<Review> findByProductAndApprovedTrue(Product product);

    // buscar todas las reseñas de un producto ordenadas por fecha
    List<Review> findByProductAndApprovedTrueOrderByCreatedAtDesc(Product product);

    // buscar reseñas de un usuario
    List<Review> findByUser(User user);

    // verificar si un usuario ya pudo una reseña a un producto
    // (para evitar multiples reseñas del mismo usuario)
    Boolean existsByUserAndProduct(User user, Product product);

    // buscar reseñas pendientes de aprobación
    List<Review> findByApprovedFalse();

    // buscar reseñas por calificación
    List<Review> findByRatingAndApprovedTrue(Integer rating);

    // calcular el promedio de calificación de un producto
    @Query("SELECT AVG(r.rating) FROM Review r WHERE " +
            "r.product.id = :productId AND r.approved = true")
    Double calculateAverageRating(@Param("productId") Long productId);

    Long countByProductAndApprovedTrue(Product product);

    // cuanta reseñas por calificacion (para graficos)
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE " +
            "r.product.id = :productId AND r.approved = true " +
            "GROUP BY r.rating " +
            "ORDER BY r.rating DESC")
    java.util.List<Object[]> countByRatingForProduct(@Param("productId") Long productId);
    // Retorna: [[5, 10], [4, 5], [3, 2], [2, 1], [1, 0]]
    // ej: 10 reseñas de 5 estrellas, 5 de 4 estrellas
}