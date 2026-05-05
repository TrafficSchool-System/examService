package com.example.examService.features.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ExamSession Entity - Represents an exam attempt by a user
 * 
 * Domain: Exam
 * Purpose: Track exam sessions including timing, questions, and user answers
 * 
 * Relationships:
 * - OneToOne with Result (exam outcome)
 * - OneToMany with Answer (user's answers during exam)
 */
@Entity
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID from UserService (microservice reference)
     */
    private Long userId;

    /**
     * When the exam started (UTC)
     */
    private LocalDateTime startsAt;

    /**
     * When the exam expires (UTC)
     * Calculated as startsAt + durationMinutes
     */
    private LocalDateTime expiresAt;

    /**
     * Whether the exam has been finished/submitted
     */
    private boolean finished;

    /**
     * JSON string containing the exam questions
     * Stored as TEXT to preserve questions even if QuizService updates them
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String questionsJson;

    /**
     * The result of this exam (if finished)
     */
    @OneToOne(mappedBy = "examSession", cascade = CascadeType.ALL)
    private Result result;

    /**
     * All answers submitted during this exam session
     */
    @OneToMany(mappedBy = "examSession", cascade = CascadeType.ALL)
    private List<Answer> answers;

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

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
}
