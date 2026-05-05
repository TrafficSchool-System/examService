package com.example.examService.features.admin.service;

import com.example.examService.features.exam.dto.ExamResultSummaryDTO;
import com.example.examService.features.exam.service.ListUserExamsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GetUserExamsUseCase (ADMIN) - Get all exams for a specific user
 * 
 * Responsibility:
 * - Allow admin to view exam history for any user
 * - Delegates to ListUserExamsUseCase (same logic, different permission
 * context)
 * 
 * Business Rules:
 * - Admin can view any user's exams
 * - Returns same data as user's own exam list
 * 
 * Dependencies:
 * - ListUserExamsUseCase - reuse existing logic
 * 
 * Flow:
 * 1. Validate userId (could add additional admin checks here)
 * 2. Delegate to ListUserExamsUseCase
 * 3. Return exam list
 * 
 * Note: This is a thin wrapper for admin context.
 * Keeps admin and user use cases separated for future authorization logic.
 */
@Service
public class GetUserExamsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetUserExamsUseCase.class);

    private final ListUserExamsUseCase listUserExamsUseCase;

    public GetUserExamsUseCase(ListUserExamsUseCase listUserExamsUseCase) {
        this.listUserExamsUseCase = listUserExamsUseCase;
    }

    /**
     * Execute: Get all exams for a specific user (admin view)
     * 
     * @param userId The user ID to get exams for
     * @return List of ExamResultSummaryDTO
     */
    public List<ExamResultSummaryDTO> execute(Long userId) {
        log.debug("[admin] Getting exams for userId={}", userId);

        // Future: Add admin-specific logging, audit trail, etc.

        // Delegate to existing use case
        List<ExamResultSummaryDTO> exams = listUserExamsUseCase.execute(userId);

        log.debug("[admin] Found {} exams for userId={}", exams.size(), userId);

        return exams;
    }
}
