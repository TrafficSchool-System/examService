package com.example.examService.features.exam.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * ExamSessionDTO - Data Transfer Object for exam session
 * 
 * Used to:
 * - Send exam questions and timing to frontend when starting an exam
 * - Return current exam status when resuming an exam
 * 
 * Contains:
 * - List of questions (from QuizService)
 * - Timing information (start time, expiration, duration)
 * - Saved answers (when resuming an exam)
 */
public class ExamSessionDTO {

    /**
     * List of questions for this exam
     * Fetched from QuizService and stored in ExamSession.questionsJson
     */
    private List<QuizQuestionDTO> questions;

    /**
     * Duration of the exam in minutes
     * Default: 50 minutes for final exam
     */
    private long durationMinutes;

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
     * Previously saved answers (when resuming)
     * Map: questionId -> selectedAnswer
     */
    private Map<Long, String> savedAnswers;

    // Constructors

    public ExamSessionDTO() {
    }

    public ExamSessionDTO(List<QuizQuestionDTO> questions, long durationMinutes,
            LocalDateTime startsAt, LocalDateTime expiresAt) {
        this.questions = questions;
        this.durationMinutes = durationMinutes;
        this.startsAt = startsAt;
        this.expiresAt = expiresAt;
    }

    // Helper methods for frontend timezone handling

    /**
     * Convert expiresAt to epoch milliseconds (UTC)
     * Allows frontend to handle timezone conversions safely
     */
    public long getExpiresAtMillis() {
        if (expiresAt == null)
            return 0;
        return expiresAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    /**
     * Convert startsAt to epoch milliseconds (UTC)
     * Allows frontend to handle timezone conversions safely
     */
    public long getStartsAtMillis() {
        if (startsAt == null)
            return 0;
        return startsAt.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    // Getters and Setters

    public List<QuizQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionDTO> questions) {
        this.questions = questions;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public Map<Long, String> getSavedAnswers() {
        return savedAnswers;
    }

    public void setSavedAnswers(Map<Long, String> savedAnswers) {
        this.savedAnswers = savedAnswers;
    }
}
