package com.example.examService.features.admin.controller;

import com.example.examService.features.admin.service.GetSystemStatsUseCase;
import com.example.examService.features.admin.service.GetUserExamsUseCase;
import com.example.examService.features.exam.dto.ExamResultSummaryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AdminExamController - Administrative operations for exams
 * 
 * Architecture:
 * - Thin controller (no business logic)
 * - Delegates to admin Use Cases
 * - All endpoints require ADMIN role
 * 
 * Endpoints (ADMIN role):
 * - GET /api/admin/exams/statistics → System-wide exam statistics
 * - GET /api/admin/users/{userId}/exams → Get exams for specific user
 */
@RestController
@RequestMapping("/api/admin/exams")
public class AdminExamController {

    private final GetSystemStatsUseCase getSystemStatsUseCase;
    private final GetUserExamsUseCase getUserExamsUseCase;

    public AdminExamController(
            GetSystemStatsUseCase getSystemStatsUseCase,
            GetUserExamsUseCase getUserExamsUseCase) {
        this.getSystemStatsUseCase = getSystemStatsUseCase;
        this.getUserExamsUseCase = getUserExamsUseCase;
    }

    /**
     * GET SYSTEM-WIDE EXAM STATISTICS
     * GET /api/admin/exams/statistics
     * 
     * Returns aggregated statistics about all exams in the system.
     * Includes active exams count and completed exams count.
     * 
     * @return Map with "activeExams" and "completedExams" counts
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Integer>> getSystemExamStatistics() {
        Map<String, Integer> statistics = getSystemStatsUseCase.execute();
        return ResponseEntity.ok(statistics);
    }

    /**
     * GET EXAMS FOR SPECIFIC USER
     * GET /api/admin/users/{userId}/exams
     * 
     * Returns all exams taken by a specific user (admin view).
     * Used for admin user management and support.
     * 
     * @param userId The user ID to get exams for
     * @return List of ExamResultSummaryDTO
     */
    @GetMapping("/users/{userId}/exams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExamResultSummaryDTO>> getUserExams(@PathVariable Long userId) {
        List<ExamResultSummaryDTO> results = getUserExamsUseCase.execute(userId);
        return ResponseEntity.ok(results);
    }
}
