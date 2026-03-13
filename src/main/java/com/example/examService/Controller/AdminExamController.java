package com.example.examService.Controller;

import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Service.ExamServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * ADMIN EXAM CONTROLLER - Administrative Operations
 * ============================================================================
 * 
 * ADMIN-ONLY endpoints for exam management and system-wide statistics.
 * These endpoints provide aggregated data and privileged operations.
 */
@RestController
@RequestMapping("/api/admin/exams")
public class AdminExamController {

    private final ExamServiceInterface examService;

    public AdminExamController(ExamServiceInterface examService) {
        this.examService = examService;
    }

    /**
     * GET SYSTEM-WIDE EXAM STATISTICS
     * GET /api/admin/exams/statistics
     * 
     * Returns aggregated statistics about all exams in the system.
     * Includes active exams count, completed exams count, etc.
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Integer>> getSystemExamStatistics() {
        Map<String, Integer> statistics = examService.getExamCounts();
        return ResponseEntity.ok(statistics);
    }

    /**
     * GET EXAMS FOR SPECIFIC USER
     * GET /api/admin/users/{userId}/exams
     * 
     * Returns all exams taken by a specific user (admin view).
     * Used for admin user management and support.
     */
    @GetMapping("/users/{userId}/exams")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExamResultSummaryDTO>> getUserExams(@PathVariable Long userId) {
        List<ExamResultSummaryDTO> results = examService.getAllExamResults(userId);
        return ResponseEntity.ok(results);
    }
}
