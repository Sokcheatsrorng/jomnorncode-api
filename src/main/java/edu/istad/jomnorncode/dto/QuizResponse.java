package edu.istad.jomnorncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;

    private String title;

    private String description;

    private String question;

    private String questionType;

    private String difficulty;

    private Long lessonId;

    private Boolean isPublished = false;

    private Integer timeLimit;

    private Integer totalQuestions;

    private Integer passingScore;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
