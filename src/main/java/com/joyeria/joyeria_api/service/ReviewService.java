package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.exception.DuplicateResourceException;
import com.joyeria.joyeria_api.exception.InvalidOperationException;
import com.joyeria.joyeria_api.exception.ResourceNotFoundException;
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

    public Review createReview(Review review) {
        // validar que no haya reseñado ya
        if (reviewRepository.existsByUserAndProduct(review.getUser(), review.getProduct())) {
            throw new DuplicateResourceException("Ya has reseñado este producto");
        }

        // validar rating
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new InvalidOperationException("La calificación debe estar entre 1 y 5 estrellas");
        }

        review.setApproved(false);

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> getApprovedReviewsByProduct(Long productId) {
        Product product = productService.getProductById(productId);
        return reviewRepository.findByProductAndApprovedTrueOrderByCreatedAtDesc(product);
    }

    @Transactional(readOnly = true)
    public List<Review> getPendingReviews() {
        return reviewRepository.findByApprovedFalse();
    }

    public Review approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));

        review.setApproved(true);
        return reviewRepository.save(review);
    }

    public Review disapproveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));

        review.setApproved(false);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        Double average = reviewRepository.calculateAverageRating(productId);
        return average != null ? average : 0.0;
    }

    @Transactional(readOnly = true)
    public Long countReviewsByProduct(Long productId) {
        Product product = productService.getProductById(productId);
        return reviewRepository.countByProductAndApprovedTrue(product);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getRatingDistribution(Long productId) {
        return reviewRepository.countByRatingForProduct(productId);
    }

    public Review updateReview(Long id, Review reviewDetails) {
        Review review = getReviewById(id);

        if (reviewDetails.getRating() != null) {
            if (reviewDetails.getRating() < 1 || reviewDetails.getRating() > 5) {
                throw new InvalidOperationException("La calificación debe estar entre 1 y 5 estrellas");
            }
            review.setRating(reviewDetails.getRating());
        }

        if (reviewDetails.getComment() != null) {
            review.setComment(reviewDetails.getComment());
        }

        review.setApproved(false);

        return reviewRepository.save(review);
    }

    public Review partialUpdateReview(Long id, Review reviewDetails) {
        Review review = getReviewById(id);

        boolean modified = false;

        if (reviewDetails.getRating() != null) {
            if (reviewDetails.getRating() < 1 || reviewDetails.getRating() > 5) {
                throw new InvalidOperationException("La calificación debe estar entre 1 y 5 estrellas");
            }
            review.setRating(reviewDetails.getRating());
            modified = true;
        }

        if (reviewDetails.getComment() != null) {
            review.setComment(reviewDetails.getComment());
            modified = true;
        }

        if (modified) {
            review.setApproved(false);
        }

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", id);
        }

        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }
}