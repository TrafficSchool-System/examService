package com.example.examService.features.exam.dto;

import java.util.List;
import java.util.Map;

/**
 * ExamResultDTO - Complete exam result with questions and answers
 * 
 * Used when: Displaying detailed exam results to user
 * Endpoint: GET /api/exams/latest/result
 * 
 * Contains:
 * - Score and pass/fail status
 * - All questions with correct answers
 * - User's submitted answers
 * - Time taken to complete exam
 */
public class ExamResultDTO {

    /**
     * Number of correct answers
     */
    private int score;

    /**
     * Whether the exam was passed (70% threshold)
     */
    private boolean passed;

    /**
     * All questions from the exam
     * Includes correct answers for review
     */
    private List<QuizQuestionDTO> questions;

    /**
     * User's submitted answers
     * Map: questionId -> user's selected answer
     */
    private Map<Long, String> userAnswers;

    /**
     * Time taken to complete the exam (in minutes)
     */
    private int timeTaken;

    // Constructors

    public ExamResultDTO() {
    }

    public ExamResultDTO(int score, boolean passed, List<QuizQuestionDTO> questions,
            Map<Long, String> userAnswers, int timeTaken) {
        this.score = score;
        this.passed = passed;
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.timeTaken = timeTaken;
    }

    // Getters and Setters

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
