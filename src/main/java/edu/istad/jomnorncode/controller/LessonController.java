package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.LessonRequest;
import edu.istad.jomnorncode.dto.LessonResponse;
import edu.istad.jomnorncode.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Lesson Management APIs")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Create a new lesson", description = "Only ADMIN and INSTRUCTOR can create lessons")
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lesson by ID")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        LessonResponse response = lessonService.getLessonById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all lessons for a course with pagination")
    public ResponseEntity<Page<LessonResponse>> getLessonsByCourse(
            @PathVariable Long courseId,
            @PageableDefault(value = 20,size = 10, sort = "lessons", direction = Sort.Direction.ASC)
            Pageable pageable) {
        Page<LessonResponse> response = lessonService.getLessonsByCourse(courseId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}/ordered")
    @Operation(summary = "Get all lessons for a course ordered by sequence")
    public ResponseEntity<List<LessonResponse>> getLessonsOrderedBySequence(@PathVariable Long courseId) {
        List<LessonResponse> response = lessonService.getLessonsOrderedBySequence(courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getLessons(
            @RequestParam(required = false, defaultValue = "true") boolean paginated,
            Pageable pageable) {

        if (!paginated) {
            List<LessonResponse> all = lessonService.getAllLessons(Pageable.unpaged())
                    .getContent();
            return ResponseEntity.ok(all);
        }

        Page<LessonResponse> paged = lessonService.getAllLessons(pageable);
        return ResponseEntity.ok(paged);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Update lesson", description = "Only ADMIN and INSTRUCTOR can update lessons")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Delete lesson", description = "Only ADMIN and INSTRUCTOR can delete lessons")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
