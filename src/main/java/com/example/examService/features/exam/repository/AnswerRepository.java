package com.example.examService.features.exam.repository;

import com.example.examService.features.exam.entity.Answer;
import com.example.examService.features.exam.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Answer entity
 * 
 * Provides queries for:
 * - Finding all answers for a specific exam session
 * - Finding all answers by a user (across all exams)
 */
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /**
     * Find all answers submitted by a specific user
     * Used for user answer history (across all exams)
     */
    List<Answer> findByUserId(Long userId);

    /**
     * Find all answers for a specific exam session
     * Used to restore saved answers when resuming an exam
     * Used to calculate exam score when finishing
     */
    List<Answer> findByExamSession(ExamSession examSession);
}
