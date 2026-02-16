package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.model.Review;
import com.joyeria.joyeria_api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // crear una nueva reseña { "productId": 1, "userId": 5, "rating": 5, "comment": "Excelente" }
    @PostMapping
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        Review created = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    // obtener reseñas aprobadas de un producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getApprovedReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    // obtener calificaciin promedio de un producto (4.5)
    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        Double average = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(average);
    }


     //contar reseñas de un producto
    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> countReviews(@PathVariable Long productId) {
        Long count = reviewService.countReviewsByProduct(productId);
        return ResponseEntity.ok(count);
    }

    //obtener distribucion de calificaciones
    //retorna: [[5, 10], [4, 5], [3, 2], [2, 1], [1, 0]]
    // significa que 10 reseñas de 5 estrellas, 5 de 4 estrellas

    @GetMapping("/product/{productId}/distribution")
    public ResponseEntity<List<Object[]>> getRatingDistribution(@PathVariable Long productId) {
        List<Object[]> distribution = reviewService.getRatingDistribution(productId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<Map<String, Object>> getProductReviewStats(@PathVariable Long productId) {
        Double average = reviewService.getAverageRating(productId);
        Long count = reviewService.countReviewsByProduct(productId);
        List<Object[]> distribution = reviewService.getRatingDistribution(productId);

        Map<String, Object> stats = Map.of(
                "average", average,
                "count", count,
                "distribution", distribution
        );

        return ResponseEntity.ok(stats);
    }


     //verificar si un usuario ya reseñó un producto

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkUserReview(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        Boolean hasReviewed = reviewService.hasUserReviewedProduct(userId, productId);
        return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
    }

    // obtener reseñas pendientes de aprobación solo para administradores

    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

    //actualizar completamente una reseña solo el usuario puede editar
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody Review review
    ) {
        Review updated = reviewService.updateReview(id, review);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Review> partialUpdateReview(
            @PathVariable Long id,
            @RequestBody Review review
    ) {
        Review updated = reviewService.partialUpdateReview(id, review);
        return ResponseEntity.ok(updated);
    }

    //aprobar una reseña solo para administradores

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable Long id) {
        Review approved = reviewService.approveReview(id);
        return ResponseEntity.ok(approved);
    }

    // desaprobar una reseña solo para administradores
    @PatchMapping("/{id}/disapprove")
    public ResponseEntity<Review> disapproveReview(@PathVariable Long id) {
        Review disapproved = reviewService.disapproveReview(id);
        return ResponseEntity.ok(disapproved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}