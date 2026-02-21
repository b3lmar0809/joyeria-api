package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.dto.CheckUserReviewResponse;
import com.joyeria.joyeria_api.dto.CreateReviewRequest;
import com.joyeria.joyeria_api.dto.ProductReviewStatsResponse;
import com.joyeria.joyeria_api.dto.RatingDistributionResponse;
import com.joyeria.joyeria_api.model.Product;
import com.joyeria.joyeria_api.model.Review;
import com.joyeria.joyeria_api.service.ProductService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Review> createReview(@Valid @RequestBody CreateReviewRequest request) {
        // convertir DTO a entidad
        Product product = productService.getProductById(request.getProductId());
        // User user = userService.getUserById(request.getUserId())
        Review review = new Review();
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review created = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getApprovedReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        Double average = reviewService.getAverageRating(productId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> countReviews(@PathVariable Long productId) {
        Long count = reviewService.countReviewsByProduct(productId);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/product/{productId}/distribution")
    public ResponseEntity<List<RatingDistributionResponse>> getRatingDistribution(
            @PathVariable Long productId
    ) {
        List<Object[]> rawDistribution = reviewService.getRatingDistribution(productId);

        // convertir List<Object[]> a List<RatingDistributionResponse>
        List<RatingDistributionResponse> distribution = rawDistribution.stream()
                .map(arr -> new RatingDistributionResponse(
                        (Integer) arr[0],  // rating
                        (Long) arr[1]      // count
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ProductReviewStatsResponse> getProductReviewStats(
            @PathVariable Long productId
    ) {
        Double average = reviewService.getAverageRating(productId);
        Long count = reviewService.countReviewsByProduct(productId);
        List<Object[]> rawDistribution = reviewService.getRatingDistribution(productId);

        // convertir distribucion a DTO
        List<RatingDistributionResponse> distribution = rawDistribution.stream()
                .map(arr -> new RatingDistributionResponse(
                        (Integer) arr[0],
                        (Long) arr[1]
                ))
                .collect(Collectors.toList());

        // crear response DTO
        ProductReviewStatsResponse stats = new ProductReviewStatsResponse(average, count, distribution);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check")
    public ResponseEntity<CheckUserReviewResponse> checkUserReview(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        Boolean hasReviewed = reviewService.hasUserReviewedProduct(userId, productId);
        CheckUserReviewResponse response = new CheckUserReviewResponse(hasReviewed);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

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

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable Long id) {
        Review approved = reviewService.approveReview(id);
        return ResponseEntity.ok(approved);
    }

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