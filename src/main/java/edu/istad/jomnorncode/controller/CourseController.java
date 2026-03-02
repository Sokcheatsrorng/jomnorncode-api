package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.CourseCreateRequest;
import edu.istad.jomnorncode.dto.CourseResponse;
import edu.istad.jomnorncode.dto.CourseUpdateRequest;
import edu.istad.jomnorncode.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {

    private final CourseService courseService;

    // ────────────────────────────────────────────────
    //  Public endpoints (no auth required)
    // ────────────────────────────────────────────────

    @GetMapping("/public")
    @Operation(summary = "Get published courses – paginated by default, add ?all=true for full list")
    public ResponseEntity<?> getPublicCourses(
            @RequestParam(required = false, defaultValue = "false") boolean all,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        if (all) {
            // Ignore pagination → get all
            Page<CourseResponse> result = courseService.getAllPublicCourses(Pageable.unpaged());
            return ResponseEntity.ok(result.getContent());  // just the list
        }

        Page<CourseResponse> courses = courseService.getAllPublicCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one course by ID (public if published)")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    // ────────────────────────────────────────────────
    //  Admin / authenticated endpoints
    // ────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get ALL courses (admin view) – no pagination here")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<CourseResponse>> getAllCoursesAdmin() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    @Operation(summary = "Create a new course")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseCreateRequest request) {
        CourseResponse created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course (partial update supported)")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseUpdateRequest request) {

        CourseResponse updated = courseService.updateCourse(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}