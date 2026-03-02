package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.EnrollmentRequest;
import edu.istad.jomnorncode.dto.EnrollmentResponse;
import edu.istad.jomnorncode.service.EnrollmentService;
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
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Enrollment Management APIs")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Enroll user in a course")
    public ResponseEntity<EnrollmentResponse> enrollUserInCourse(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollUserInCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment by ID")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse response = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all enrollments for a user with pagination")
    public ResponseEntity<Page<EnrollmentResponse>> getEnrollmentsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<EnrollmentResponse> response = enrollmentService.getEnrollmentsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all enrollments for a course with pagination")
    public ResponseEntity<Page<EnrollmentResponse>> getEnrollmentsByCourse(
            @PathVariable Long courseId,
            Pageable pageable) {
        Page<EnrollmentResponse> response = enrollmentService.getEnrollmentsByCourse(courseId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all enrollments with pagination")
    public ResponseEntity<Page<EnrollmentResponse>> getAllEnrollments(Pageable pageable) {
        Page<EnrollmentResponse> response = enrollmentService.getAllEnrollments(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/course/{courseId}")
    @Operation(summary = "Get total enrollment count for a course")
    public ResponseEntity<Long> countEnrollmentsByCourse(@PathVariable Long courseId) {
        long count = enrollmentService.countEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Get total enrollment count for a user")
    public ResponseEntity<Long> countEnrollmentsByUser(@PathVariable Long userId) {
        long count = enrollmentService.countEnrollmentsByUser(userId);
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/progress")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Update enrollment progress")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentProgress(
            @PathVariable Long id,
            @RequestParam Integer progressPercentage) {
        EnrollmentResponse response = enrollmentService.updateEnrollmentProgress(id, progressPercentage);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Mark enrollment as completed")
    public ResponseEntity<EnrollmentResponse> completeEnrollment(@PathVariable Long id) {
        EnrollmentResponse response = enrollmentService.completeEnrollment(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/{courseId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Unenroll user from course")
    public ResponseEntity<Void> unenrollUserFromCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        enrollmentService.unenrollUserFromCourse(userId, courseId);
        return ResponseEntity.noContent().build();
    }
}
