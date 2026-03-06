package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.UserProgressRequest;
import edu.istad.jomnorncode.dto.UserProgressResponse;
import edu.istad.jomnorncode.entity.Lesson;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.entity.UserProgress;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.LessonRepository;
import edu.istad.jomnorncode.repository.UserProgressRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    public UserProgressResponse recordProgress(UserProgressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + request.getLessonId()));

        UserProgress progress = new UserProgress();
        progress.setUser(user);
        progress.setLesson(lesson);
        progress.setStatus(request.getStatus());
        progress.setWatchTime(request.getWatchedDuration());
        progress.setCompletionDate(request.isCompleted() ? LocalDateTime.now() : null);
        progress.setCreatedAt(LocalDateTime.now());

        UserProgress savedProgress = userProgressRepository.save(progress);
        return mapToResponse(savedProgress);
    }

    @Transactional(readOnly = true)
    public UserProgressResponse getProgressById(Long id) {
        UserProgress progress = userProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User progress not found with id: " + id));
        return mapToResponse(progress);
    }

    @Transactional(readOnly = true)
    public Page<UserProgressResponse> getProgressByUser(Long userId, Pageable pageable) {
        return userProgressRepository.findByUserUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserProgressResponse> getProgressByLesson(Long lessonId, Pageable pageable) {
        return userProgressRepository.findByLessonLessonId(lessonId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserProgressResponse> getProgressByUserAndLesson(Long userId, Long lessonId, Pageable pageable) {
        return userProgressRepository.findByUserUserIdAndLessonLessonId(userId, lessonId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserProgressResponse> getAllProgress(Pageable pageable) {
        return userProgressRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public long getCompletedLessonsCountByUser(Long userId) {
        return userProgressRepository.countByUserUserIdAndCompletedAtIsNotNull(userId);
    }

    @Transactional(readOnly = true)
    public Double calculateCourseProgressPercentage(Long userId, Long courseId) {
        // This would require aggregating lesson completion for a course
        return userProgressRepository.calculateCourseProgress(userId, courseId);
    }

    public UserProgressResponse updateProgress(Long id, UserProgressRequest request) {
        UserProgress progress = userProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User progress not found with id: " + id));

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            progress.setStatus(request.getStatus());
        }
        if (request.getWatchedDuration() != null) {
            progress.setWatchTime(request.getWatchedDuration());
        }
        if (request.isCompleted() && progress.getIsCompleted() == null) {
            progress.setCompletionDate(LocalDateTime.now());
        }
        progress.setUpdatedAt(LocalDateTime.now());

        UserProgress updatedProgress = userProgressRepository.save(progress);
        return mapToResponse(updatedProgress);
    }

    public UserProgressResponse markAsComplete(Long id) {
        UserProgress progress = userProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User progress not found with id: " + id));

        progress.setStatus("COMPLETED");
        progress.setCompletionDate(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());

        UserProgress updatedProgress = userProgressRepository.save(progress);
        return mapToResponse(updatedProgress);
    }

    public void deleteProgress(Long id) {
        UserProgress progress = userProgressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User progress not found with id: " + id));
        userProgressRepository.delete(progress);
    }

    private UserProgressResponse mapToResponse(UserProgress progress) {
        return UserProgressResponse.builder()
                .id(progress.getProgressId())
                .userId(progress.getUser().getUserId())
                .lessonId(progress.getLesson().getLessonId())
                .status(progress.getStatus())
                .watchedDuration(progress.getWatchTime())
                .completedAt(progress.getCompletionDate())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
}
