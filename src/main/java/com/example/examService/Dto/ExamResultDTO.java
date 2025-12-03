package com.example.examService.Dto;

import java.util.List;
import java.util.Map;

public class ExamResultDTO {
    private int score;
    private boolean passed;
    private List<QuizQuestionDTO> questions;
    private Map<Long, String> userAnswers; // questionId -> userAnswer
    private int timeTaken; // minuter

    public ExamResultDTO() {
    }

    public ExamResultDTO(int score, boolean passed, List<QuizQuestionDTO> questions, Map<Long, String> userAnswers,
            int timeTaken) {
        this.score = score;
        this.passed = passed;
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.timeTaken = timeTaken;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public List<QuizQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionDTO> questions) {
        this.questions = questions;
    }

    public Map<Long, String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Map<Long, String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }
}
