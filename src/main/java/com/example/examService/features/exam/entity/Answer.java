package com.example.examService.features.exam.entity;

import jakarta.persistence.*;

/**
 * Answer Entity - Represents a single answer submitted during an exam
 * 
 * Domain: Exam
 * Purpose: Track individual question answers and their correctness
 * 
 * Relationships:
 * - ManyToOne with ExamSession (each answer belongs to one exam session)
 */
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID from UserService (microservice reference)
     */
    private Long userId;

    /**
     * Question ID from QuizService
     */
    private Long questionId;

    /**
     * The answer text selected by the user
     */
    private String selectedAnswer;

    /**
     * Whether the selected answer was correct
     * Validated against the correct answer from questionsJson
     */
    private boolean correct;

    /**
     * The exam session this answer belongs to
     */
    @ManyToOne
    @JoinColumn(name = "exam_session_id")
    private ExamSession examSession;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public ExamSession getExamSession() {
        return examSession;
    }

    public void setExamSession(ExamSession examSession) {
        this.examSession = examSession;
    }
}
