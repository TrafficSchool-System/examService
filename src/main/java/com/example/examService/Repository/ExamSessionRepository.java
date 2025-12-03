package com.example.examService.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.examService.Entity.ExamSession;

public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
    Optional<ExamSession> findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(Long userId);

    Optional<ExamSession> findTopByUserIdAndFinishedTrueOrderByStartsAtDesc(Long userId);
}
