package com.example.examService.features.exam.service;

import com.example.examService.features.exam.dto.ExamSessionDTO;
import com.example.examService.features.exam.dto.QuizQuestionDTO;
import com.example.examService.features.exam.entity.Answer;
import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.repository.AnswerRepository;
import com.example.examService.features.exam.repository.ExamSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GetActiveExamUseCase - Get the active (ongoing) exam for a user
 * 
 * Responsibility:
 * - Find active exam session for user
 * - Load stored questions from JSON
 * - Load previously saved answers (for resume functionality)
 * - Return complete exam state to frontend
 * 
 * Business Rules:
 * - Returns null if no active exam exists
 * - Includes all saved answers for resuming exam
 * - Questions come from stored JSON (not QuizService) to ensure consistency
 * 
 * Dependencies:
 * - ExamSessionRepository - find active session
 * - AnswerRepository - load saved answers
 * - ObjectMapper - deserialize questions from JSON
 * 
 * Flow:
 * 1. Find active (unfinished) exam session for user
 * 2. Deserialize questions from questionsJson
 * 3. Load all saved answers for this session
 * 4. Build ExamSessionDTO with questions, timing, and saved answers
 * 5. Return to frontend for display/resume
 */
@Service
public class GetActiveExamUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetActiveExamUseCase.class);

    private final ExamSessionRepository examSessionRepository;
    private final AnswerRepository answerRepository;
    private final ObjectMapper objectMapper;

    private static final long EXAM_DURATION_MINUTES = 50;

    public GetActiveExamUseCase(ExamSessionRepository examSessionRepository,
            AnswerRepository answerRepository,
            ObjectMapper objectMapper) {
        this.examSessionRepository = examSessionRepository;
        this.answerRepository = answerRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Execute: Get active exam session for user
     * 
     * @param userId The user ID from UserService
     * @return ExamSessionDTO with questions and saved answers, or null if no active
     *         exam
     */
    public ExamSessionDTO execute(Long userId) {
        log.debug("Getting active exam for userId={}", userId);

        // Step 1: Find active exam session
        ExamSession session = examSessionRepository
                .findTopByUserIdAndFinishedFalseOrderByStartsAtDesc(userId)
                .orElse(null);

        if (session == null) {
            log.debug("No active exam found for userId={}", userId);
            return null;
        }

        log.debug("Found active session id={} for userId={}", session.getId(), userId);

        // Step 2: Deserialize questions from JSON
        List<QuizQuestionDTO> questions = deserializeQuestions(session.getQuestionsJson());

        // Step 3: Load saved answers
        Map<Long, String> savedAnswers = loadSavedAnswers(session);

        // Step 4: Build DTO
        ExamSessionDTO dto = new ExamSessionDTO();
        dto.setQuestions(questions);
        dto.setDurationMinutes(EXAM_DURATION_MINUTES);
        dto.setStartsAt(session.getStartsAt());
        dto.setExpiresAt(session.getExpiresAt());
        dto.setSavedAnswers(savedAnswers);

        log.debug("Returning active exam with {} saved answers for userId={}", savedAnswers.size(), userId);

        return dto;
    }

    /**
     * Deserialize questions from JSON string
     * 
     * @param questionsJson JSON string from ExamSession.questionsJson
     * @return List of QuizQuestionDTO
     */
    private List<QuizQuestionDTO> deserializeQuestions(String questionsJson) {
        try {
            return objectMapper.readValue(
                    questionsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, QuizQuestionDTO.class));
        } catch (Exception e) {
            log.error("Failed to deserialize questions from JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Could not read questions from JSON", e);
        }
    }

    /**
     * Load all saved answers for the exam session
     * 
     * @param session The exam session
     * @return Map of questionId -> selectedAnswer
     */
    private Map<Long, String> loadSavedAnswers(ExamSession session) {
        List<Answer> answers = answerRepository.findByExamSession(session);

        log.debug("Found {} saved answers for sessionId={}", answers.size(), session.getId());

        return answers.stream()
                .collect(Collectors.toMap(
                        Answer::getQuestionId,
                        Answer::getSelectedAnswer));
    }
}
