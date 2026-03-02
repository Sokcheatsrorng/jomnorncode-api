package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.UserProgressRequest;
import edu.istad.jomnorncode.dto.UserProgressResponse;
import edu.istad.jomnorncode.service.UserProgressService;
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
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "User Progress", description = "User Progress Tracking APIs")
public class UserProgressController {

    private final UserProgressService userProgressService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Record user progress on a lesson")
    public ResponseEntity<UserProgressResponse> recordProgress(@Valid @RequestBody UserProgressRequest request) {
        UserProgressResponse response = userProgressService.recordProgress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get progress record by ID")
    public ResponseEntity<UserProgressResponse> getProgressById(@PathVariable Long id) {
        UserProgressResponse response = userProgressService.getProgressById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all progress records for a user with pagination")
    public ResponseEntity<Page<UserProgressResponse>> getProgressByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<UserProgressResponse> response = userProgressService.getProgressByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get all progress records for a lesson with pagination")
    public ResponseEntity<Page<UserProgressResponse>> getProgressByLesson(
            @PathVariable Long lessonId,
            Pageable pageable) {
        Page<UserProgressResponse> response = userProgressService.getProgressByLesson(lessonId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/lesson/{lessonId}")
    @Operation(summary = "Get progress record for a specific user and lesson")
    public ResponseEntity<Page<UserProgressResponse>> getProgressByUserAndLesson(
            @PathVariable Long userId,
            @PathVariable Long lessonId,
            Pageable pageable) {
        Page<UserProgressResponse> response = userProgressService.getProgressByUserAndLesson(userId, lessonId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all progress records with pagination")
    public ResponseEntity<Page<UserProgressResponse>> getAllProgress(Pageable pageable) {
        Page<UserProgressResponse> response = userProgressService.getAllProgress(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/completed-count")
    @Operation(summary = "Get count of completed lessons for a user")
    public ResponseEntity<Long> getCompletedLessonsCountByUser(@PathVariable Long userId) {
        long count = userProgressService.getCompletedLessonsCountByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/course/{courseId}/progress-percentage")
    @Operation(summary = "Calculate course progress percentage for a user")
    public ResponseEntity<Double> calculateCourseProgressPercentage(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        Double progressPercentage = userProgressService.calculateCourseProgressPercentage(userId, courseId);
        return ResponseEntity.ok(progressPercentage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update progress record")
    public ResponseEntity<UserProgressResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody UserProgressRequest request) {
        UserProgressResponse response = userProgressService.updateProgress(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Mark a lesson as completed")
    public ResponseEntity<UserProgressResponse> markAsComplete(@PathVariable Long id) {
        UserProgressResponse response = userProgressService.markAsComplete(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete progress record")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long id) {
        userProgressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}
