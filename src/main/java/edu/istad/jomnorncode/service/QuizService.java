package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.QuizRequest;
import edu.istad.jomnorncode.dto.QuizResponse;
import edu.istad.jomnorncode.entity.Lesson;
import edu.istad.jomnorncode.entity.Quiz;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.LessonRepository;
import edu.istad.jomnorncode.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;

    public QuizResponse createQuiz(QuizRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + request.getLessonId()));

        Quiz quiz = Quiz.builder()
                .quizTitle(request.getTitle())
                .description(request.getDescription())
                .passingScore(request.getPassingScore())
                .timeLimit(request.getTimeLimit())
                .isPublished(false)
                .lesson(lesson)
                .build();

        return mapToResponse(quizRepository.save(quiz));
    }

    public QuizResponse updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        if (request.getTitle() != null)       quiz.setQuizTitle(request.getTitle());
        if (request.getDescription() != null) quiz.setDescription(request.getDescription());
        if (request.getPassingScore() != null) quiz.setPassingScore(request.getPassingScore());
        if (request.getTimeLimit() != null)   quiz.setTimeLimit(request.getTimeLimit());

        return mapToResponse(quizRepository.save(quiz));
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<QuizResponse> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<QuizResponse> getQuizzesByLesson(Long lessonId, Pageable pageable) {
        return quizRepository.findByLessonLessonId(lessonId, pageable).map(this::mapToResponse);
    }

    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        quizRepository.delete(quiz);
    }

    private QuizResponse mapToResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getQuizId())
                .title(quiz.getQuizTitle())
                .description(quiz.getDescription())
                .passingScore(quiz.getPassingScore())
                .timeLimit(quiz.getTimeLimit())
                .totalQuestions(quiz.getTotalQuestions())
                .isPublished(quiz.getIsPublished())
                .lessonId(quiz.getLesson().getLessonId())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }
}