package com.example.examService.features.exam.service;

import com.example.examService.features.exam.dto.ExamResultDTO;
import com.example.examService.features.exam.dto.QuizQuestionDTO;
import com.example.examService.features.exam.entity.Answer;
import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.entity.Result;
import com.example.examService.features.exam.repository.AnswerRepository;
import com.example.examService.features.exam.repository.ExamSessionRepository;
import com.example.examService.features.exam.repository.ResultRepository;
import com.example.examService.shared.exception.ExamNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GetExamResultUseCase - Get detailed result for the latest finished exam
 * 
 * Responsibility:
 * - Find latest finished exam session
 * - Load exam result (score, pass/fail)
 * - Load all questions from session
 * - Load user's answers
 * - Calculate time taken
 * - Return complete exam result for review
 * 
 * Business Rules:
 * - Returns latest finished exam only
 * - Includes all questions with correct answers for learning
 * - Shows user's submitted answers for comparison
 * - Calculates time taken from start to finish
 * 
 * Dependencies:
 * - ExamSessionRepository - find finished session
 * - ResultRepository - get exam result
 * - AnswerRepository - get user answers
 * - ObjectMapper - deserialize questions
 * 
 * Flow:
 * 1. Find latest finished exam session for user
 * 2. Find result for that session
 * 3. Deserialize questions from session JSON
 * 4. Load user's answers
 * 5. Calculate time taken (startsAt to finishedAt)
 * 6. Build ExamResultDTO
 */
@Service
public class GetExamResultUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetExamResultUseCase.class);

    private final ExamSessionRepository examSessionRepository;
    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;
    private final ObjectMapper objectMapper;

    public GetExamResultUseCase(ExamSessionRepository examSessionRepository,
            ResultRepository resultRepository,
            AnswerRepository answerRepository,
            ObjectMapper objectMapper) {
        this.examSessionRepository = examSessionRepository;
        this.resultRepository = resultRepository;
        this.answerRepository = answerRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Execute: Get latest exam result for user
     * 
     * @param userId User ID from UserService
     * @return ExamResultDTO with complete exam data
     * @throws ExamNotFoundException if no finished exam exists
     */
    public ExamResultDTO execute(Long userId) {
        log.debug("Getting exam result for userId={}", userId);

        // Step 1: Find latest finished session
        ExamSession session = findLatestFinishedSession(userId);

        log.debug("Found finished session id={} for userId={}", session.getId(), userId);

        // Step 2: Get result
        Result result = findResultForSession(session);

        // Step 3: Deserialize questions
        List<QuizQuestionDTO> questions = deserializeQuestions(session.getQuestionsJson());

        // Step 4: Load user answers
        Map<Long, String> userAnswers = loadUserAnswers(session);

        log.debug("Found {} answers for sessionId={}", userAnswers.size(), session.getId());

        // Step 5: Calculate time taken
        int timeTaken = calculateTimeTaken(session.getStartsAt(), result.getFinishedAt());

        // Step 6: Build DTO
        ExamResultDTO dto = new ExamResultDTO(
                result.getScore(),
                result.isPassed(),
                questions,
                userAnswers,
                timeTaken);

        log.info("Exam result retrieved for userId={} score={} passed={}", userId, result.getScore(),
                result.isPassed());

        return dto;
    }

    /**
     * Find the latest finished exam session for user
     * 
     * @param userId User ID
     * @return Latest finished ExamSession
     * @throws ExamNotFoundException if no finished exam exists
     */
    private ExamSession findLatestFinishedSession(Long userId) {
        return examSessionRepository
                .findTopByUserIdAndFinishedTrueOrderByStartsAtDesc(userId)
                .orElseThrow(() -> new ExamNotFoundException("No finished exam found for user"));
    }

    /**
     * Find result for exam session
     * 
     * @param session ExamSession
     * @return Result entity
     * @throws ExamNotFoundException if no result exists
     */
    private Result findResultForSession(ExamSession session) {
        return resultRepository.findByExamSession(session)
                .orElseThrow(() -> new ExamNotFoundException("No result found for exam session"));
    }

    /**
     * Deserialize questions from JSON
     * 
     * @param questionsJson JSON string
     * @return List of QuizQuestionDTO
     */
    private List<QuizQuestionDTO> deserializeQuestions(String questionsJson) {
        try {
            return objectMapper.readValue(
                    questionsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, QuizQuestionDTO.class));
        } catch (Exception e) {
            System.err.println("❌ [GetExamResultUseCase] Failed to deserialize questions");
            throw new RuntimeException("Could not read questions from JSON", e);
        }
    }

    /**
     * Load user's answers for the session
     * 
     * @param session ExamSession
     * @return Map of questionId -> selectedAnswer
     */
    private Map<Long, String> loadUserAnswers(ExamSession session) {
        List<Answer> answers = answerRepository.findByExamSession(session);

        return answers.stream()
                .collect(Collectors.toMap(
                        Answer::getQuestionId,
                        Answer::getSelectedAnswer));
    }

    /**
     * Calculate time taken to complete exam (in minutes)
     * 
     * @param startsAt   When exam started
     * @param finishedAt When exam was finished
     * @return Time in minutes
     */
    private int calculateTimeTaken(java.time.LocalDateTime startsAt, java.time.LocalDateTime finishedAt) {
        Duration duration = Duration.between(startsAt, finishedAt);
        return (int) duration.toMinutes();
    }
}
