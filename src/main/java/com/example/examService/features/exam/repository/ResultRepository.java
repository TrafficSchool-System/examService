package com.example.examService.features.exam.repository;

import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Result entity
 * 
 * Provides queries for:
 * - Finding the result for a specific exam session
 */
public interface ResultRepository extends JpaRepository<Result, Long> {

    /**
     * Find the result for a specific exam session
     * Used to retrieve exam outcome after completion
     */
    Optional<Result> findByExamSession(ExamSession examSession);
}
