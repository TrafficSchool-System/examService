package com.example.examService.features.exam.repository;

import com.example.examService.features.exam.entity.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ExamSession entity
 * 
 * Provides custom queries for:
 * - Finding active/finished exam sessions by user
 * - Counting ongoing vs completed exams (admin statistics)
 */
public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {

    /**
     * Find the latest active (unfinished) exam session for a user
     * Used to resume an exam or prevent multiple active exams
     */
    Optional<ExamSession> findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(Long userId);

    /**
     * Find the latest finished exam session for a user
     * Used to display the most recent exam result
     */
    Optional<ExamSession> findTopByUserIdAndFinishedTrueOrderByStartsAtDesc(Long userId);

    /**
     * Find all finished exam sessions for a user, ordered by most recent first
     * Used for exam history display
     */
    List<ExamSession> findAllByUserIdAndFinishedTrueOrderByStartsAtDesc(Long userId);

    /**
     * Count all ongoing (unfinished) exams across all users
     * Used for admin statistics dashboard
     */
    long countByFinishedFalse();

    /**
     * Count all completed exams across all users
     * Used for admin statistics dashboard
     */
    long countByFinishedTrue();
}
