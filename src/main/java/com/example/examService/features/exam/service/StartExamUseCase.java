package com.example.examService.features.exam.service;

import com.example.examService.features.exam.dto.ExamSessionDTO;
import com.example.examService.features.exam.dto.QuizQuestionDTO;
import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.repository.ExamSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * StartExamUseCase - Start a new exam session for a user
 * 
 * Responsibility:
 * - Close any previous active exam sessions
 * - Fetch exam questions from QuizService
 * - Create new exam session with timing
 * - Store questions as JSON for consistency
 * 
 * Business Rules:
 * - Only one active exam per user at a time
 * - Exam duration: 50 minutes
 * - Questions are fetched from QuizService and stored to prevent changes during
 * exam
 * 
 * Dependencies:
 * - WebClient (quizService) - fetch questions
 * - ExamSessionRepository - store exam session
 * - ObjectMapper - serialize questions to JSON
 * 
 * Flow:
 * 1. Close any active exam session for user
 * 2. Fetch 70 questions from QuizService GET /final-exam
 * 3. Create new ExamSession with timing (startsAt, expiresAt)
 * 4. Save questions as JSON in session
 * 5. Return ExamSessionDTO to frontend
 */
@Service
public class StartExamUseCase {

    private static final Logger log = LoggerFactory.getLogger(StartExamUseCase.class);

    private final WebClient quizWebClient;
    private final ExamSessionRepository examSessionRepository;
    private final ObjectMapper objectMapper;

    private static final long EXAM_DURATION_MINUTES = 50;

    public StartExamUseCase(WebClient quizWebClient,
            ExamSessionRepository examSessionRepository,
            ObjectMapper objectMapper) {
        this.quizWebClient = quizWebClient;
        this.examSessionRepository = examSessionRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Execute: Start a new exam for the user
     * 
     * @param userId The user ID from UserService
     * @return ExamSessionDTO with questions and timing
     * @throws RuntimeException if QuizService fails to provide questions
     */
    public ExamSessionDTO execute(Long userId) {
        log.info("Starting exam for userId={}", userId);

        // Step 1: Close any active exam sessions
        closeActiveExamSessions(userId);

        // Step 2: Fetch questions from QuizService
        ExamSessionDTO examFromQuizService = fetchQuestionsFromQuizService();

        // Step 3: Create new exam session
        ExamSession session = createExamSession(userId, examFromQuizService);

        // Step 4: Save questions as JSON
        saveQuestionsAsJson(session, examFromQuizService.getQuestions());

        // Step 5: Save session to database
        examSessionRepository.save(session);

        // Step 6: Set timing in DTO
        examFromQuizService.setStartsAt(session.getStartsAt());
        examFromQuizService.setExpiresAt(session.getExpiresAt());

        log.info("Exam started sessionId={} userId={} expiresAt={}", session.getId(), userId, session.getExpiresAt());

        return examFromQuizService;
    }

    /**
     * Close any active (unfinished) exam sessions for the user
     * Business rule: Only one active exam per user
     */
    private void closeActiveExamSessions(Long userId) {
        examSessionRepository.findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                .ifPresent(oldSession -> {
                    log.debug("Closing old active exam session id={} for userId={}", oldSession.getId(), userId);
                    oldSession.setFinished(true);
                    examSessionRepository.save(oldSession);
                });
    }

    /**
     * Fetch exam questions from QuizService
     * Uses WebClient with @LoadBalanced and X-Internal-API-Key header
     * 
     * @return ExamSessionDTO from QuizService with questions
     * @throws RuntimeException if QuizService call fails
     */
    private ExamSessionDTO fetchQuestionsFromQuizService() {
        log.debug("Calling QuizService GET /final-exam");

        try {
            ExamSessionDTO exam = quizWebClient.get()
                    .uri("/final-exam")
                    .retrieve()
                    .bodyToMono(ExamSessionDTO.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            log.debug("Received {} questions from QuizService", exam.getQuestions().size());
            return exam;

        } catch (Exception e) {
            log.error("Failed to fetch questions from QuizService: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch exam questions from QuizService", e);
        }
    }

    /**
     * Create new ExamSession entity with timing
     * 
     * @param userId  User ID from UserService
     * @param examDTO Exam data from QuizService
     * @return New ExamSession (not yet persisted)
     */
    private ExamSession createExamSession(Long userId, ExamSessionDTO examDTO) {
        LocalDateTime now = LocalDateTime.now(); // UTC

        ExamSession session = new ExamSession();
        session.setUserId(userId);
        session.setStartsAt(now);
        session.setExpiresAt(now.plusMinutes(EXAM_DURATION_MINUTES));
        session.setFinished(false);

        return session;
    }

    /**
     * Save questions as JSON string in ExamSession
     * This ensures questions remain consistent even if QuizService updates them
     * 
     * @param session   ExamSession to store questions in
     * @param questions List of questions from QuizService
     */
    private void saveQuestionsAsJson(ExamSession session, List<QuizQuestionDTO> questions) {
        try {
            String questionsJson = objectMapper.writeValueAsString(questions);
            session.setQuestionsJson(questionsJson);
        } catch (Exception e) {
            log.error("Failed to serialize questions to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save questions as JSON", e);
        }
    }
}
