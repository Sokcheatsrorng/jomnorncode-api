package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.CategoryResponse;
import edu.istad.jomnorncode.dto.CourseCreateRequest;
import edu.istad.jomnorncode.dto.CourseResponse;
import edu.istad.jomnorncode.dto.CourseUpdateRequest;
import edu.istad.jomnorncode.entity.Category;
import edu.istad.jomnorncode.entity.Course;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.CategoryRepository;
import edu.istad.jomnorncode.repository.CourseRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // ===================== CREATE =====================
    public CourseResponse createCourse(CourseCreateRequest request) {
        log.info("Creating new course: {}", request.getCourseTitle());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));



        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseTitle(request.getCourseTitle())
                .description(request.getDescription())
                .category(category)
                .thumbnailUrl(request.getThumbnailUrl())
                .price(request.getPrice())
                .discountPercentage(request.getDiscountPercentage())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .build();

        Course savedCourse = courseRepository.save(course);
        return CourseResponse.fromEntity(savedCourse);
    }

    // ===================== READ =====================
    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return CourseResponse.fromEntity(course);
    }

    public Page<CourseResponse> getAllPublicCourses(Pageable pageable) {
        return courseRepository.findByIsPublishedTrue(pageable)
                .map(CourseResponse::fromEntity);
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .toList();
    }

    // ===================== UPDATE =====================
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (request.getCourseCode() != null) course.setCourseCode(request.getCourseCode());
        if (request.getCourseTitle() != null) course.setCourseTitle(request.getCourseTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getThumbnailUrl() != null) course.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getDiscountPercentage() != null) course.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getIsPublished() != null) course.setIsPublished(request.getIsPublished());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            course.setCategory(category);
        }

//        if (request.getInstructorId() != null) {
//            User instructor = userRepository.findById(request.getInstructorId())
//                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
//            course.setInstructor(instructor);
//        }

        Course updated = courseRepository.save(course);
        return CourseResponse.fromEntity(updated);
    }

    // ===================== DELETE =====================
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        courseRepository.deleteById(courseId);
        log.info("Course deleted successfully: {}", courseId);
    }

    // Optional helper
    public Optional<Course> getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

}