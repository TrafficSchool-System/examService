package com.example.examService.features.exam.dto;

import jakarta.validation.constraints.NotNull;

/**
 * SubmitAnswerRequest - Request DTO for submitting an answer
 * 
 * Used when: User selects an answer during an active exam
 * Endpoint: POST /api/exams/active/answers
 * 
 * Validation:
 * - questionId must not be null
 * - selectedAnswer must not be null
 */
public class SubmitAnswerRequest {

    /**
     * The question ID from QuizService
     */
    @NotNull(message = "Question ID is required")
    private Long questionId;

    /**
     * The answer text selected by the user
     * Must match one of the answers from QuizQuestionDTO.answers
     */
    @NotNull(message = "Selected answer is required")
    private String selectedAnswer;

    // Constructors

    public SubmitAnswerRequest() {
    }

    public SubmitAnswerRequest(Long questionId, String selectedAnswer) {
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
    }

    // Getters and Setters

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}
