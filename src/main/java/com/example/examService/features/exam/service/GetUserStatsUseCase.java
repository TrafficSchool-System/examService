package com.example.examService.features.exam.service;

import com.example.examService.features.exam.dto.ExamResultSummaryDTO;
import com.example.examService.features.exam.dto.ExamStatsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GetUserStatsUseCase - Calculate user's exam statistics
 * 
 * Responsibility:
 * - Get all user's exam results
 * - Calculate total/passed/failed counts
 * - Calculate average percentage
 * - Calculate current and best streak
 * - Determine readiness for real exam
 * 
 * Business Rules:
 * - Current streak: consecutive passed exams from most recent
 * - Best streak: highest consecutive pass count ever
 * - Ready for real exam: current streak >= 5 AND all last 5 exams >= 80%
 * - Average percentage: mean of all exam percentages
 * 
 * Dependencies:
 * - ListUserExamsUseCase - get exam history
 * 
 * Flow:
 * 1. Get all exam results via ListUserExamsUseCase
 * 2. Count total, passed, failed
 * 3. Calculate average percentage
 * 4. Calculate current streak (from most recent backwards)
 * 5. Calculate best streak (iterate all exams)
 * 6. Determine readiness (last 5 exams all passed with >= 80%)
 * 7. Return ExamStatsDTO
 */
@Service
public class GetUserStatsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetUserStatsUseCase.class);

    private final ListUserExamsUseCase listUserExamsUseCase;

    private static final int READY_STREAK_THRESHOLD = 5;
    private static final int READY_PERCENTAGE_THRESHOLD = 80;

    public GetUserStatsUseCase(ListUserExamsUseCase listUserExamsUseCase) {
        this.listUserExamsUseCase = listUserExamsUseCase;
    }

    /**
     * Execute: Calculate user's exam statistics
     * 
     * @param userId User ID from UserService
     * @return ExamStatsDTO with complete statistics
     */
    public ExamStatsDTO execute(Long userId) {
        log.debug("Calculating stats for userId={}", userId);

        // Step 1: Get all exam results
        List<ExamResultSummaryDTO> results = listUserExamsUseCase.execute(userId);

        if (results.isEmpty()) {
            log.debug("No exams found for userId={}, returning empty stats", userId);
            return createEmptyStats();
        }

        // Step 2: Count total, passed, failed
        int totalExams = results.size();
        int passedExams = countPassedExams(results);
        int failedExams = totalExams - passedExams;

        // Step 3: Calculate average percentage
        int averagePercentage = calculateAveragePercentage(results);

        // Step 4: Calculate current streak (from most recent)
        int currentStreak = calculateCurrentStreak(results);

        // Step 5: Calculate best streak
        int bestStreak = calculateBestStreak(results);

        // Step 6: Determine readiness
        boolean readyForRealExam = isReadyForRealExam(results, currentStreak);

        // Step 7: Build DTO
        ExamStatsDTO stats = new ExamStatsDTO(
                totalExams,
                passedExams,
                failedExams,
                averagePercentage,
                currentStreak,
                bestStreak,
                readyForRealExam);

        log.debug("Stats for userId={}: total={} passed={} avg={}% streak={} ready={}",
                userId, totalExams, passedExams, averagePercentage, currentStreak, readyForRealExam);

        return stats;
    }

    /**
     * Create empty stats (no exams taken)
     */
    private ExamStatsDTO createEmptyStats() {
        return new ExamStatsDTO(0, 0, 0, 0, 0, 0, false);
    }

    /**
     * Count number of passed exams
     */
    private int countPassedExams(List<ExamResultSummaryDTO> results) {
        return (int) results.stream()
                .filter(ExamResultSummaryDTO::isPassed)
                .count();
    }

    /**
     * Calculate average percentage across all exams
     */
    private int calculateAveragePercentage(List<ExamResultSummaryDTO> results) {
        return (int) results.stream()
                .mapToInt(ExamResultSummaryDTO::getPercentage)
                .average()
                .orElse(0);
    }

    /**
     * Calculate current streak (consecutive passed exams from most recent)
     * Stops at first failed exam
     */
    private int calculateCurrentStreak(List<ExamResultSummaryDTO> results) {
        int streak = 0;
        for (ExamResultSummaryDTO result : results) {
            if (result.isPassed()) {
                streak++;
            } else {
                break; // Stop at first fail
            }
        }
        return streak;
    }

    /**
     * Calculate best streak ever (highest consecutive pass count)
     */
    private int calculateBestStreak(List<ExamResultSummaryDTO> results) {
        int bestStreak = 0;
        int currentStreak = 0;

        for (ExamResultSummaryDTO result : results) {
            if (result.isPassed()) {
                currentStreak++;
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak;
                }
            } else {
                currentStreak = 0; // Reset on fail
            }
        }

        return bestStreak;
    }

    /**
     * Determine if user is ready for real exam
     * 
     * Criteria:
     * - Current streak >= 5 consecutive passed exams
     * - All last 5 exams have percentage >= 80%
     * 
     * @param results       Exam results
     * @param currentStreak Current consecutive pass streak
     * @return true if ready, false otherwise
     */
    private boolean isReadyForRealExam(List<ExamResultSummaryDTO> results, int currentStreak) {
        // Must have at least 5 consecutive passes
        if (currentStreak < READY_STREAK_THRESHOLD) {
            return false;
        }

        // All last 5 exams must have >= 80%
        return results.stream()
                .limit(READY_STREAK_THRESHOLD)
                .allMatch(r -> r.isPassed() && r.getPercentage() >= READY_PERCENTAGE_THRESHOLD);
    }
}
