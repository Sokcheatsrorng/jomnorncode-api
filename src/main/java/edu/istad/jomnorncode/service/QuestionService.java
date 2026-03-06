package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.QuestionRequest;
import edu.istad.jomnorncode.dto.QuestionResponse;
import edu.istad.jomnorncode.entity.Question;
import edu.istad.jomnorncode.entity.Quiz;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.QuestionRepository;
import edu.istad.jomnorncode.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionResponse createQuestion(QuestionRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Quiz not found with id: " + request.getQuizId()));

        // Shuffle correct + incorrect answers together
        List<String> choices = new ArrayList<>(request.getIncorrectAnswers());
        choices.add(request.getCorrectAnswer());
        Collections.shuffle(choices);

        Question question = Question.builder()
                .quiz(quiz)
                .question(request.getQuestion())
                .correctAnswer(request.getCorrectAnswer())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .choices(choices)
                .build();

        // Update total questions count on quiz
        quiz.setTotalQuestions(questionRepository.findByQuizQuizId(quiz.getQuizId()).size() + 1);
        quizRepository.save(quiz);

        return mapToResponse(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> getQuestionsByQuiz(Long quizId, Pageable pageable) {
        return questionRepository.findByQuizQuizId(quizId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long id) {
        return questionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    public QuestionResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        if (request.getQuestion() != null)     question.setQuestion(request.getQuestion());
        if (request.getCorrectAnswer() != null) question.setCorrectAnswer(request.getCorrectAnswer());
        if (request.getCategory() != null)     question.setCategory(request.getCategory());
        if (request.getDifficulty() != null)   question.setDifficulty(request.getDifficulty());

        if (request.getIncorrectAnswers() != null) {
            List<String> choices = new ArrayList<>(request.getIncorrectAnswers());
            choices.add(request.getCorrectAnswer());
            Collections.shuffle(choices);
            question.setChoices(choices);
        }

        return mapToResponse(questionRepository.save(question));
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        questionRepository.delete(question);
    }

    private QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .quizId(question.getQuiz().getQuizId())
                .question(question.getQuestion())
                .correctAnswer(question.getCorrectAnswer())
                .category(question.getCategory())
                .difficulty(question.getDifficulty())
                .choices(question.getChoices())
                .build();
    }
}