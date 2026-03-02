package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Corrected methods (using CourseCourseId)
    boolean existsByUserUserIdAndCourseCourseId(Long userId, Long courseId);

    Optional<Enrollment> findByUserUserIdAndCourseCourseId(Long userId, Long courseId);

    Page<Enrollment> findByUserUserId(Long userId, Pageable pageable);

    Page<Enrollment> findByCourseCourseId(Long courseId, Pageable pageable);

    long countByCourseCourseId(Long courseId);

    long countByUserUserId(Long userId);

    List<Enrollment> findByUserUserId(Long userId);


}