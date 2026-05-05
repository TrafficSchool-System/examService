package com.example.examService.features.exam.service;

import com.example.examService.features.exam.entity.Answer;
import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.entity.Result;
import com.example.examService.features.exam.repository.AnswerRepository;
import com.example.examService.features.exam.repository.ExamSessionRepository;
import com.example.examService.features.exam.repository.ResultRepository;
import com.example.examService.shared.exception.ExamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FinishExamUseCase - Finish and submit an exam
 * 
 * Responsibility:
 * - Find active exam session
 * - Calculate score from submitted answers
 * - Determine pass/fail status (70% threshold)
 * - Create Result entity
 * - Mark exam session as finished
 * 
 * Business Rules:
 * - Pass threshold: 70% (49 out of 70 questions)
 * - Score is calculated from Answer.correct field
 * - Only answers for THIS session count (not all user answers)
 * - ExamSession must be saved before Result (foreign key constraint)
 * 
 * Dependencies:
 * - ExamSessionRepository - find and update session
 * - AnswerRepository - get answers for scoring
 * - ResultRepository - save result
 * 
 * Flow:
 * 1. Find active exam session
 * 2. Get all answers for this session
 * 3. Calculate score (count correct answers)
 * 4. Determine if passed (>= 70%)
 * 5. Create Result entity
 * 6. Mark session as finished
 * 7. Save session (MUST be before result due to FK)
 * 8. Save result
 */
@Service
public class FinishExamUseCase {

    private static final Logger log = LoggerFactory.getLogger(FinishExamUseCase.class);

    private final ExamSessionRepository examSessionRepository;
    private final AnswerRepository answerRepository;
    private final ResultRepository resultRepository;

    private static final double PASS_THRESHOLD = 0.7; // 70%

    public FinishExamUseCase(ExamSessionRepository examSessionRepository,
            AnswerRepository answerRepository,
            ResultRepository resultRepository) {
        this.examSessionRepository = examSessionRepository;
        this.answerRepository = answerRepository;
        this.resultRepository = resultRepository;
    }

    /**
     * Execute: Finish exam and calculate result
     * 
     * @param userId User ID from UserService
     * @throws ExamNotFoundException if no active exam exists
     */
    public void execute(Long userId) {
        log.info("Finishing exam for userId={}", userId);

        // Step 1: Find active exam session
        ExamSession session = findActiveExamSession(userId);

        log.debug("Found active session id={} userId={}", session.getId(), userId);

        // Step 2: Get all answers for this session
        List<Answer> answers = answerRepository.findByExamSession(session);

        log.debug("Total answers submitted: {} for sessionId={}", answers.size(), session.getId());

        // Step 3: Calculate score
        int score = calculateScore(answers);
        int totalQuestions = answers.size();

        log.debug("Score: {}/{} for userId={}", score, totalQuestions, userId);

        // Step 4: Determine if passed
        boolean passed = isPassed(score, totalQuestions);

        log.info("Exam finished userId={} score={}/{} passed={}", userId, score, totalQuestions, passed);

        // Step 5: Create result
        Result result = createResult(score, passed);

        // Step 6: Mark session as finished and link result
        session.setFinished(true);
        result.setExamSession(session);

        // Step 7: Save session FIRST (foreign key requirement)
        examSessionRepository.save(session);

        // Step 8: Save result
        resultRepository.save(result);

        log.debug("Exam result saved for userId={} sessionId={}", userId, session.getId());
    }

    /**
     * Find active exam session for user
     * 
     * @param userId User ID
     * @return Active ExamSession
     * @throws ExamNotFoundException if no active exam exists
     */
    private ExamSession findActiveExamSession(Long userId) {
        return examSessionRepository
                .findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                .orElseThrow(() -> new ExamNotFoundException("No active exam session found for user"));
    }

    /**
     * Calculate score by counting correct answers
     * 
     * @param answers List of answers for the exam
     * @return Number of correct answers
     */
    private int calculateScore(List<Answer> answers) {
        return (int) answers.stream()
                .filter(Answer::isCorrect)
                .count();
    }

    /**
     * Determine if exam is passed
     * Threshold: 70% (49 out of 70 questions)
     * 
     * @param score          Number of correct answers
     * @param totalQuestions Total questions in exam
     * @return true if passed, false otherwise
     */
    private boolean isPassed(int score, int totalQuestions) {
        if (totalQuestions == 0) {
            return false;
        }
        double percentage = (double) score / totalQuestions;
        return percentage >= PASS_THRESHOLD;
    }

    /**
     * Create Result entity
     * 
     * @param score  Number of correct answers
     * @param passed Whether exam was passed
     * @return New Result (not yet persisted)
     */
    private Result createResult(int score, boolean passed) {
        Result result = new Result();
        result.setScore(score);
        result.setPassed(passed);
        result.setFinishedAt(LocalDateTime.now()); // UTC
        return result;
    }
}
