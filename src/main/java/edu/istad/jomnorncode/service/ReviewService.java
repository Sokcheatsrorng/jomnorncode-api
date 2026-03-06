package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.ReviewRequest;
import edu.istad.jomnorncode.dto.ReviewResponse;
import edu.istad.jomnorncode.entity.Course;
import edu.istad.jomnorncode.entity.Review;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.CourseRepository;
import edu.istad.jomnorncode.repository.ReviewRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public ReviewResponse createReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setUser(user);
        review.setCourse(course);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return mapToResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByCourse(Long courseId, Pageable pageable) {
        return reviewRepository.findByCourseCourseIdOrderByCreatedAtDesc(courseId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingByCourse(Long courseId) {
        return reviewRepository.findAverageRatingByCourseCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public long getReviewCountByCourse(Long courseId) {
        return reviewRepository.countByCourseCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByCourseAndRating(Long courseId, Integer rating, Pageable pageable) {
        return reviewRepository.findByCourseCourseIdAndRating(courseId, rating, pageable)
                .map(this::mapToResponse);
    }

    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null && !request.getComment().isEmpty()) {
            review.setComment(request.getComment());
        }
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        return mapToResponse(updatedReview);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        reviewRepository.delete(review);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getReviewId())
                .userId(review.getUser().getUserId())
                .userName(review.getUser().getUsername())
                .courseId(review.getCourse().getCourseId())
                .courseName(review.getCourse().getCourseTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
