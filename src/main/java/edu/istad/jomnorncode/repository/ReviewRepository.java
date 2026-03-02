package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourseCourseId(Long courseId);
    List<Review> findByUserUserId(Long userId);

    Page<Review> findByCourseCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);

    Page<Review> findByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Double findAverageRatingByCourseCourseId(Long courseId);

    Page<Review> findByCourseCourseIdAndRating(Long courseId, Integer rating, Pageable pageable);

    long countByCourseCourseId(Long courseId);
}
