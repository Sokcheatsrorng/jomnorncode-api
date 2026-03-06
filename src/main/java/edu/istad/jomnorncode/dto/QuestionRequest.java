package edu.istad.jomnorncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class QuestionRequest {

    @NotNull(message = "quizId is required")
    private Long quizId;

    @NotBlank(message = "question is required")
    private String question;

    @NotBlank(message = "correctAnswer is required")
    private String correctAnswer;

    private String category;
    private String difficulty;

    @NotEmpty(message = "incorrectAnswers is required")
    private List<String> incorrectAnswers;

}