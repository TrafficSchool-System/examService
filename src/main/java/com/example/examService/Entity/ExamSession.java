package com.example.examService.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // koppling till userService

    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;

    private boolean finished;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String questionsJson;

    // relation till resultat
    @OneToOne(mappedBy = "examSession", cascade = CascadeType.ALL)
    private Result result;

    // relation till svar
    @OneToMany(mappedBy = "examSession", cascade = CascadeType.ALL)
    private java.util.List<Answer> answers;

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

    public java.util.List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(java.util.List<Answer> answers) {
        this.answers = answers;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
}