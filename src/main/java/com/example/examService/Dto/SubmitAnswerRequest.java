package com.example.examService.Dto;

import jakarta.validation.constraints.NotNull;

public class SubmitAnswerRequest {

    @NotNull(message = "Question ID är obligatoriskt")
    private Long questionId;

    @NotNull(message = "Selected answer är obligatoriskt")
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
