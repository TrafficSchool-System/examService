package com.example.examService.features.exam.service;

import com.example.examService.features.exam.dto.ExamResultSummaryDTO;
import com.example.examService.features.exam.entity.Answer;
import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.entity.Result;
import com.example.examService.features.exam.repository.AnswerRepository;
import com.example.examService.features.exam.repository.ExamSessionRepository;
import com.example.examService.features.exam.repository.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ListUserExamsUseCase - List all finished exams for a user
 * 
 * Responsibility:
 * - Find all finished exam sessions for user
 * - Load results for each session
 * - Calculate summary data (score, percentage, time taken)
 * - Return list ordered by most recent first
 * 
 * Business Rules:
 * - Only finished exams are included
 * - Ordered by startsAt descending (newest first)
 * - Percentage calculated as (score / totalQuestions) * 100
 * - Sessions without results are skipped
 * 
 * Dependencies:
 * - ExamSessionRepository - find finished sessions
 * - ResultRepository - get results
 * - AnswerRepository - count total questions
 * 
 * Flow:
 * 1. Find all finished sessions for user
 * 2. For each session:
 * a. Get result
 * b. Count total questions from answers
 * c. Calculate time taken
 * d. Calculate percentage
 * e. Create ExamResultSummaryDTO
 * 3. Filter out sessions without results
 * 4. Return list
 */
@Service
public class ListUserExamsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListUserExamsUseCase.class);

    private final ExamSessionRepository examSessionRepository;
    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;

    public ListUserExamsUseCase(ExamSessionRepository examSessionRepository,
            ResultRepository resultRepository,
            AnswerRepository answerRepository) {
        this.examSessionRepository = examSessionRepository;
        this.resultRepository = resultRepository;
        this.answerRepository = answerRepository;
    }

    /**
     * Execute: List all finished exams for user
     * 
     * @param userId User ID from UserService
     * @return List of ExamResultSummaryDTO (newest first)
     */
    public List<ExamResultSummaryDTO> execute(Long userId) {
        log.debug("Listing finished exams for userId={}", userId);

        // Step 1: Find all finished sessions
        List<ExamSession> sessions = examSessionRepository
                .findAllByUserIdAndFinishedTrueOrderByStartsAtDesc(userId);

        log.debug("Found {} finished exam sessions for userId={}", sessions.size(), userId);

        // Step 2: Convert to DTOs
        List<ExamResultSummaryDTO> results = sessions.stream()
                .map(this::createSummaryDTO)
                .filter(dto -> dto != null) // Skip sessions without results
                .collect(Collectors.toList());

        log.debug("Returning {} exam summaries for userId={}", results.size(), userId);

        return results;
    }

    /**
     * Create ExamResultSummaryDTO for a session
     * 
     * @param session ExamSession
     * @return ExamResultSummaryDTO or null if no result exists
     */
    private ExamResultSummaryDTO createSummaryDTO(ExamSession session) {
        // Get result for session
        Result result = resultRepository.findByExamSession(session)
                .orElse(null);

        if (result == null) {
            log.debug("No result for sessionId={}, skipping", session.getId());
            return null;
        }

        // Count total questions from answers
        int totalQuestions = answerRepository.findByExamSession(session).size();

        // Calculate time taken (minutes)
        int timeTaken = calculateTimeTaken(session.getStartsAt(), result.getFinishedAt());

        // Calculate percentage
        int percentage = calculatePercentage(result.getScore(), totalQuestions);

        // Create DTO
        ExamResultSummaryDTO dto = new ExamResultSummaryDTO(
                result.getId(),
                result.getScore(),
                totalQuestions,
                result.isPassed(),
                timeTaken,
                result.getFinishedAt());
        dto.setPercentage(percentage);

        return dto;
    }

    /**
     * Calculate time taken (in minutes)
     * 
     * @param startsAt   When exam started
     * @param finishedAt When exam finished
     * @return Time in minutes
     */
    private int calculateTimeTaken(java.time.LocalDateTime startsAt, java.time.LocalDateTime finishedAt) {
        Duration duration = Duration.between(startsAt, finishedAt);
        return (int) duration.toMinutes();
    }

    /**
     * Calculate score percentage
     * 
     * @param score          Number of correct answers
     * @param totalQuestions Total questions in exam
     * @return Percentage (0-100)
     */
    private int calculatePercentage(int score, int totalQuestions) {
        if (totalQuestions == 0) {
            return 0;
        }
        return (int) Math.round(((double) score / totalQuestions) * 100);
    }
}
