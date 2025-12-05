package com.example.examService.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ExamSessionDTO {

    private List<QuizQuestionDTO> questions;
    private long durationMinutes;
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
    private Map<Long, String> savedAnswers; // questionId -> selectedAnswer

    
    public ExamSessionDTO() {
    }

    public ExamSessionDTO(List<QuizQuestionDTO> questions, long durationMinutes, LocalDateTime startsAt,
            LocalDateTime expiresAt) {
        this.questions = questions;
        this.durationMinutes = durationMinutes;
        this.startsAt = startsAt;
        this.expiresAt = expiresAt;
    }

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