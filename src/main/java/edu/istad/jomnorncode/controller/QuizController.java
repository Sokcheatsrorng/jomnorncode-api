package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.QuizRequest;
import edu.istad.jomnorncode.dto.QuizResponse;
import edu.istad.jomnorncode.service.QuizService;
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
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Quiz Management APIs")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Create a new quiz", description = "Only ADMIN and INSTRUCTOR can create quizzes")
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) {
        QuizResponse response = quizService.getQuizById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get all quizzes for a lesson with pagination")
    public ResponseEntity<Page<QuizResponse>> getQuizzesByLesson(
            @PathVariable Long lessonId,
            Pageable pageable) {
        Page<QuizResponse> response = quizService.getQuizzesByLesson(lessonId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all quizzes with pagination")
    public ResponseEntity<Page<QuizResponse>> getAllQuizzes(Pageable pageable) {
        Page<QuizResponse> response = quizService.getAllQuizzes(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get quizzes by difficulty level")
    public ResponseEntity<Page<QuizResponse>> getQuizzesByDifficulty(
            @PathVariable String difficulty,
            Pageable pageable) {
        Page<QuizResponse> response = quizService.getQuizzesByDifficulty(difficulty, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{questionType}")
    @Operation(summary = "Get quizzes by question type")
    public ResponseEntity<Page<QuizResponse>> getQuizzesByType(
            @PathVariable String questionType,
            Pageable pageable) {
        Page<QuizResponse> response = quizService.getQuizzesByType(questionType, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lesson/{lessonId}/count")
    @Operation(summary = "Get quiz count for a lesson")
    public ResponseEntity<Long> getQuizCountByLesson(@PathVariable Long lessonId) {
        long count = quizService.getQuizCountByLesson(lessonId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Update quiz", description = "Only ADMIN and INSTRUCTOR can update quizzes")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Delete quiz", description = "Only ADMIN and INSTRUCTOR can delete quizzes")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
