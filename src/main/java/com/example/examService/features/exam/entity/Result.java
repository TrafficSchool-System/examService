package com.example.examService.features.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Result Entity - Represents the final outcome of an exam
 * 
 * Domain: Exam
 * Purpose: Store exam results including score, pass/fail status, and completion
 * time
 * 
 * Relationships:
 * - OneToOne with ExamSession (each exam has one result)
 */
@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Number of correct answers
     */
    private int score;

    /**
     * Whether the exam was passed
     * Pass threshold: 70% (49 out of 70 questions)
     */
    private boolean passed;

    /**
     * When the exam was completed and submitted (UTC)
     */
    private LocalDateTime finishedAt;

    /**
     * The exam session this result belongs to
     */
    @OneToOne
    @JoinColumn(name = "exam_session_id")
    private ExamSession examSession;

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

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public ExamSession getExamSession() {
        return examSession;
    }

    public void setExamSession(ExamSession examSession) {
        this.examSession = examSession;
    }
}
