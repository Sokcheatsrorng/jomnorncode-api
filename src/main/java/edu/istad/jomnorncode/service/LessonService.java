package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.CourseResponse;
import edu.istad.jomnorncode.dto.LessonRequest;
import edu.istad.jomnorncode.dto.LessonResponse;
import edu.istad.jomnorncode.entity.Course;
import edu.istad.jomnorncode.entity.Lesson;
import edu.istad.jomnorncode.repository.CourseRepository;
import edu.istad.jomnorncode.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        Lesson lesson = new Lesson();
        lesson.setLessonTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setVideoDuration(request.getDuration());
        lesson.setSequenceNumber(request.getSequenceNumber());
        lesson.setCourse(course);
        lesson.setCreatedAt(LocalDateTime.now());

        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToResponse(savedLesson);
    }

    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        return mapToResponse(lesson);
    }

    @Transactional(readOnly = true)
    public Page<LessonResponse> getLessonsByCourse(Long courseId, Pageable pageable) {
        return lessonRepository.findByCourseCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsOrderedBySequence(Long courseId) {
        return lessonRepository.findByCourseCourseIdOrderBySequenceNumberAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<LessonResponse> getAllLessons(Pageable pageable) {
        return lessonRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            lesson.setLessonTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            lesson.setDescription(request.getDescription());
        }
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            lesson.setContent(request.getContent());
        }
        if (request.getVideoUrl() != null && !request.getVideoUrl().isEmpty()) {
            lesson.setVideoUrl(request.getVideoUrl());
        }
        if (request.getDuration() != null) {
            lesson.setDuration(request.getDuration());
        }
        if (request.getSequenceNumber() != null) {
            lesson.setSequenceNumber(request.getSequenceNumber());
        }
        lesson.setUpdatedAt(LocalDateTime.now());

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToResponse(updatedLesson);
    }

    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        lessonRepository.delete(lesson);
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getLessonId())
                .title(lesson.getLessonTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .duration(lesson.getDuration())
                .sequenceNumber(lesson.getSequenceNumber())
                .course(CourseResponse.fromEntity(lesson.getCourse()))
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
