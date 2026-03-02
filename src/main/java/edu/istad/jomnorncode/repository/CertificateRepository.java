package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.dto.CertificateResponse;
import edu.istad.jomnorncode.entity.Certificate;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByCertificateNumber(String certificateNumber);

    Page<Certificate> findByUserUserId(Long userId, Pageable pageable);

    Page<Certificate> findByCourseCourseId(Long courseId, Pageable pageable);

    boolean existsByUserUserIdAndCourseCourseId(@NotNull(message = "User ID is required") Long userId, @NotNull(message = "Course ID is required") Long courseId);


    long countByUserUserId(Long userId);

    long countByCourseCourseId(Long courseId);
}