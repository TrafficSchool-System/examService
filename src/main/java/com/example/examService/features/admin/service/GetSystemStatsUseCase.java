package com.example.examService.features.admin.service;

import com.example.examService.features.exam.repository.ExamSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * GetSystemStatsUseCase - Get system-wide exam statistics (ADMIN)
 * 
 * Responsibility:
 * - Count total active (ongoing) exams across all users
 * - Count total completed exams across all users
 * - Return as simple key-value map
 * 
 * Business Rules:
 * - Active exams: finished = false
 * - Completed exams: finished = true
 * - System-wide counts (not user-specific)
 * 
 * Dependencies:
 * - ExamSessionRepository - count queries
 * 
 * Flow:
 * 1. Count active exams (finished = false)
 * 2. Count completed exams (finished = true)
 * 3. Return as Map<String, Integer>
 */
@Service
public class GetSystemStatsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetSystemStatsUseCase.class);

    private final ExamSessionRepository examSessionRepository;

    public GetSystemStatsUseCase(ExamSessionRepository examSessionRepository) {
        this.examSessionRepository = examSessionRepository;
    }

    /**
     * Execute: Get system-wide exam counts
     * 
     * @return Map with "activeExams" and "completedExams" counts
     */
    public Map<String, Integer> execute() {
        log.debug("Calculating system-wide exam statistics");

        // Count active exams
        int activeExams = (int) examSessionRepository.countByFinishedFalse();

        // Count completed exams
        int completedExams = (int) examSessionRepository.countByFinishedTrue();

        // Build response map
        Map<String, Integer> stats = new HashMap<>();
        stats.put("activeExams", activeExams);
        stats.put("completedExams", completedExams);

        log.debug("System stats: activeExams={} completedExams={}", activeExams, completedExams);

        return stats;
    }
}
