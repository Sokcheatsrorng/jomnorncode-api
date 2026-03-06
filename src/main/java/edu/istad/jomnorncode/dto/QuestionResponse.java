package edu.istad.jomnorncode.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class QuestionResponse {
    private Long id;

    private Long quizId;

    private String question;

    private String correctAnswer;

    private String category;

    private String difficulty;

    private List<String> choices;
}