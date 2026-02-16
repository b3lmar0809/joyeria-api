package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.model.Review;
import com.joyeria.joyeria_api.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    //crear una nueva reseña
    public Review createReview(Review review) {
        // validar que el usuario no haya reseñado ya este producto
        if (reviewRepository.existsByUserAndProduct(review.getUser(), review.getProduct())) {
            throw new RuntimeException("Ya has reseñado este producto");
        }

        // validar rating (1-5 estrellas)
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5 estrellas");
        }

        // por defecto las reseñas requieren aprobación (moderación)
        review.setApproved(false);

        return reviewRepository.save(review);
    }

    //obtener reseñas aprobadas de un producto
    @Transactional(readOnly = true)
    public List<Review> getApprovedReviewsByProduct(Long productId) {
        // Verificar que el producto existe
        Product product = productService.getProductById(productId);

        return reviewRepository.findByProductAndApprovedTrueOrderByCreatedAtDesc(product);
    }

    //obtener todas las reseñas de un usuario
    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(Long userId) {
        throw new RuntimeException("Método no implementado aún");
    }

    //obtener reseñas pendientes de aprobacion solo para administradores
    @Transactional(readOnly = true)
    public List<Review> getPendingReviews() {
        return reviewRepository.findByApprovedFalse();
    }

    //aprobar una reseña solo para administradores
    public Review approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID: " + id));

        review.setApproved(true);
        return reviewRepository.save(review);
    }

    //Rechazar/Desaprobar una reseña solo para administradores
    public Review disapproveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID: " + id));

        review.setApproved(false);
        return reviewRepository.save(review);
    }

    //btener reseña por ID
    @Transactional(readOnly = true)
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID: " + id));
    }

    //calcular calificacion promedio de un producto
    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        Double average = reviewRepository.calculateAverageRating(productId);
        // Si no hay reseñas, retornar 0.0 en vez de null
        return average != null ? average : 0.0;
    }

    //contar cuantas reseñas tiene un producto
    @Transactional(readOnly = true)
    public Long countReviewsByProduct(Long productId) {
        Product product = productService.getProductById(productId);
        return reviewRepository.countByProductAndApprovedTrue(product);
    }

    //obtener distribucion de calificaciones de un producto

    @Transactional(readOnly = true)
    public List<Object[]> getRatingDistribution(Long productId) {
        return reviewRepository.countByRatingForProduct(productId);
    }

    //actualizar una reseña existente solo el autor puede editar
    public Review updateReview(Long id, Review reviewDetails) {
        Review review = getReviewById(id);

        if (reviewDetails.getRating() != null) {
            if (reviewDetails.getRating() < 1 || reviewDetails.getRating() > 5) {
                throw new RuntimeException("La calificación debe estar entre 1 y 5 estrellas");
            }
            review.setRating(reviewDetails.getRating());
        }

        // actualizar comentario si viene
        if (reviewDetails.getComment() != null) {
            review.setComment(reviewDetails.getComment());
        }

        // si se edita, volver a poner como no aprobada (moderacipn)
        review.setApproved(false);

        return reviewRepository.save(review);
    }

    public Review partialUpdateReview(Long id, Review reviewDetails) {
        Review review = getReviewById(id);

        boolean modified = false;

        if (reviewDetails.getRating() != null) {
            if (reviewDetails.getRating() < 1 || reviewDetails.getRating() > 5) {
                throw new RuntimeException("La calificación debe estar entre 1 y 5 estrellas");
            }
            review.setRating(reviewDetails.getRating());
            modified = true;
        }

        if (reviewDetails.getComment() != null) {
            review.setComment(reviewDetails.getComment());
            modified = true;
        }

        // Si se modificó, requiere nueva aprobación
        if (modified) {
            review.setApproved(false);
        }

        return reviewRepository.save(review);
    }

    //eliminar una reseña

    public void deleteReview(Long id) {
        // verificar que existe
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Reseña no encontrada con ID: " + id);
        }
        reviewRepository.deleteById(id);
    }

    //verificar si un usuario ya reseño un producto para el frontend para deshabilitar el boton de reseña
    @Transactional(readOnly = true)
    public Boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }
}