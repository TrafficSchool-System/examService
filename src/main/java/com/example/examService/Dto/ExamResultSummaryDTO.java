package com.example.examService.Dto;

import java.time.LocalDateTime;

public class ExamResultSummaryDTO {

    private Long id;
    private int score;
    private int totalQuestions;
    private boolean passed;
    private int timeTaken; // minuter
    private LocalDateTime finishedAt;
    private int percentage;

    // Konstruktor utan percentage (beräknas i service)
    public ExamResultSummaryDTO(Long id, int score, int totalQuestions, boolean passed, int timeTaken,
            LocalDateTime finishedAt) {
        this.id = id;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.timeTaken = timeTaken;
        this.finishedAt = finishedAt;
    }

    // Konstruktor med percentage (för framtida användning)
    public ExamResultSummaryDTO(Long id, int score, int totalQuestions, boolean passed, int timeTaken,
            LocalDateTime finishedAt, int percentage) {
        this.id = id;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.timeTaken = timeTaken;
        this.finishedAt = finishedAt;
        this.percentage = percentage;

    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

}
