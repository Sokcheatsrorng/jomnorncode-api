package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.QuizRequest;
import edu.istad.jomnorncode.dto.QuizResponse;
import edu.istad.jomnorncode.entity.Lesson;
import edu.istad.jomnorncode.entity.Quiz;
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
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + request.getLessonId()));

        Quiz quiz = new Quiz();
        quiz.setQuizTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setQuestion(request.getQuestion());
        quiz.setQuestionType(request.getQuestionType());
        quiz.setDifficulty(request.getDifficulty());
        quiz.setLesson(lesson);
        quiz.setCreatedAt(LocalDateTime.now());

        Quiz savedQuiz = quizRepository.save(quiz);
        return mapToResponse(savedQuiz);
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        return mapToResponse(quiz);
    }

    @Transactional(readOnly = true)
    public Page<QuizResponse> getQuizzesByLesson(Long lessonId, Pageable pageable) {
        return quizRepository.findByLessonLessonId(lessonId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<QuizResponse> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<QuizResponse> getQuizzesByDifficulty(String difficulty, Pageable pageable) {
        return quizRepository.findByDifficulty(difficulty, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<QuizResponse> getQuizzesByType(String questionType, Pageable pageable) {
        return quizRepository.findByQuestionType(questionType, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public long getQuizCountByLesson(Long lessonId) {
        return quizRepository.countByLessonLessonId(lessonId);
    }

    public QuizResponse updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            quiz.setQuizTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            quiz.setDescription(request.getDescription());
        }
        if (request.getQuestion() != null && !request.getQuestion().isEmpty()) {
            quiz.setQuestion(request.getQuestion());
        }
        if (request.getQuestionType() != null && !request.getQuestionType().isEmpty()) {
            quiz.setQuestionType(request.getQuestionType());
        }
        if (request.getDifficulty() != null && !request.getDifficulty().isEmpty()) {
            quiz.setDifficulty(request.getDifficulty());
        }
        quiz.setUpdatedAt(LocalDateTime.now());

        Quiz updatedQuiz = quizRepository.save(quiz);
        return mapToResponse(updatedQuiz);
    }

    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        quizRepository.delete(quiz);
    }

    private QuizResponse mapToResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getQuizId())
                .title(quiz.getQuizTitle())
                .description(quiz.getDescription())
                .question(quiz.getQuestion())
                .questionType(quiz.getQuestionType())
                .difficulty(quiz.getDifficulty())
                .lessonId(quiz.getLesson().getLessonId())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }
}
