package com.example.examService.features.exam.dto;

import java.time.LocalDateTime;

/**
 * ExamResultSummaryDTO - Summary view of an exam result
 * 
 * Used when: Displaying exam history list
 * Endpoint: GET /api/exams
 * 
 * Lighter version of ExamResultDTO without full question data
 */
public class ExamResultSummaryDTO {

    /**
     * Exam session ID
     */
    private Long id;

    /**
     * Number of correct answers
     */
    private int score;

    /**
     * Total number of questions in the exam
     */
    private int totalQuestions;

    /**
     * Whether the exam was passed (70% threshold)
     */
    private boolean passed;

    /**
     * Time taken to complete (in minutes)
     */
    private int timeTaken;

    /**
     * When the exam was completed (UTC)
     */
    private LocalDateTime finishedAt;

    /**
     * Score as percentage (0-100)
     */
    private int percentage;

    // Constructors

    /**
     * Constructor without percentage (calculated in service layer)
     */
    public ExamResultSummaryDTO(Long id, int score, int totalQuestions, boolean passed,
            int timeTaken, LocalDateTime finishedAt) {
        this.id = id;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.timeTaken = timeTaken;
        this.finishedAt = finishedAt;
    }

    /**
     * Constructor with percentage
     */
    public ExamResultSummaryDTO(Long id, int score, int totalQuestions, boolean passed,
            int timeTaken, LocalDateTime finishedAt, int percentage) {
        this.id = id;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.timeTaken = timeTaken;
        this.finishedAt = finishedAt;
        this.percentage = percentage;
    }

    // Getters and Setters

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
