package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.ReviewRequest;
import edu.istad.jomnorncode.dto.ReviewResponse;
import edu.istad.jomnorncode.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review Management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create a new review", description = "Users can review courses they have taken")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all reviews for a course with pagination")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByCourse(
            @PathVariable Long courseId,
            Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getReviewsByCourse(courseId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reviews by a user with pagination")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getReviewsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all reviews with pagination")
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}/average-rating")
    @Operation(summary = "Get average rating for a course")
    public ResponseEntity<Double> getAverageRatingByCourse(@PathVariable Long courseId) {
        Double averageRating = reviewService.getAverageRatingByCourse(courseId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/course/{courseId}/count")
    @Operation(summary = "Get total review count for a course")
    public ResponseEntity<Long> getReviewCountByCourse(@PathVariable Long courseId) {
        long count = reviewService.getReviewCountByCourse(courseId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/course/{courseId}/rating/{rating}")
    @Operation(summary = "Get reviews by course and rating")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByCourseAndRating(
            @PathVariable Long courseId,
            @PathVariable Integer rating,
            Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getReviewsByCourseAndRating(courseId, rating, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update review", description = "Users can update their own reviews")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete review", description = "Users can delete their own reviews")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
