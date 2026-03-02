package edu.istad.jomnorncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    private String description;

    @NotBlank(message = "Question is required")
    private String question;

    private String questionType;

    private String difficulty;

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;
}
