package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.EnrollmentRequest;
import edu.istad.jomnorncode.dto.EnrollmentResponse;
import edu.istad.jomnorncode.entity.Course;
import edu.istad.jomnorncode.entity.Enrollment;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.CourseRepository;
import edu.istad.jomnorncode.repository.EnrollmentRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentResponse enrollUserInCourse(EnrollmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // Check if already enrolled
        if (enrollmentRepository.existsByUserUserIdAndCourseCourseId(request.getUserId(), request.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgressPercentage(0);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(savedEnrollment);
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return mapToResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByUser(Long userId, Pageable pageable) {
        return enrollmentRepository.findByUserUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByCourse(Long courseId, Pageable pageable) {
        return enrollmentRepository.findByCourseCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getAllEnrollments(Pageable pageable) {
        return enrollmentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public long countEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.countByCourseCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public long countEnrollmentsByUser(Long userId) {
        return enrollmentRepository.countByUserUserId(userId);
    }

    public EnrollmentResponse updateEnrollmentProgress(Long id, Integer progressPercentage) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        enrollment.setProgressPercentage(progressPercentage);
        enrollment.setUpdatedAt(LocalDateTime.now());

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(updatedEnrollment);
    }

    public EnrollmentResponse completeEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));

        enrollment.setProgressPercentage(100);
        enrollment.setCompletionDate(LocalDateTime.now());
        enrollment.setUpdatedAt(LocalDateTime.now());

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(updatedEnrollment);
    }

    public void unenrollUserFromCourse(Long userId, Long courseId) {
        // Change this line (currently line 113)
        Enrollment enrollment = enrollmentRepository.findByUserUserIdAndCourseCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for user: " + userId + " and course: " + courseId));
        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getEnrollmentId())
                .userId(enrollment.getUser().getUserId())
                .courseId(enrollment.getCourse().getCourseId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
