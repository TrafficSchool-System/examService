package com.example.examService.features.exam.service;

import com.example.examService.features.exam.dto.QuizQuestionDTO;
import com.example.examService.features.exam.entity.Answer;
import com.example.examService.features.exam.entity.ExamSession;
import com.example.examService.features.exam.repository.AnswerRepository;
import com.example.examService.features.exam.repository.ExamSessionRepository;
import com.example.examService.shared.exception.ExamNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SaveAnswerUseCase - Save or update a user's answer for a question
 * 
 * Responsibility:
 * - Find active exam session
 * - Find or create Answer entity for the question
 * - Validate answer correctness against stored questions
 * - Save/update answer in database
 * 
 * Business Rules:
 * - User can change answer multiple times before finishing exam
 * - Correctness is validated immediately (for instant feedback capability)
 * - Answers are tied to specific exam session (not just user)
 * 
 * Dependencies:
 * - ExamSessionRepository - find active session
 * - AnswerRepository - save/update answer
 * - ObjectMapper - deserialize questions for validation
 * 
 * Flow:
 * 1. Find active exam session for user
 * 2. Find existing answer for question, or create new
 * 3. Update selected answer
 * 4. Validate correctness against stored questions
 * 5. Save answer to database
 */
@Service
public class SaveAnswerUseCase {

    private static final Logger log = LoggerFactory.getLogger(SaveAnswerUseCase.class);

    private final ExamSessionRepository examSessionRepository;
    private final AnswerRepository answerRepository;
    private final ObjectMapper objectMapper;

    public SaveAnswerUseCase(ExamSessionRepository examSessionRepository,
            AnswerRepository answerRepository,
            ObjectMapper objectMapper) {
        this.examSessionRepository = examSessionRepository;
        this.answerRepository = answerRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Execute: Save or update an answer for a question
     * 
     * @param userId         User ID from UserService
     * @param questionId     Question ID from QuizService
     * @param selectedAnswer The answer text selected by user
     * @throws ExamNotFoundException if no active exam exists
     */
    public void execute(Long userId, Long questionId, String selectedAnswer) {
        log.debug("Saving answer: userId={} questionId={}", userId, questionId);

        // Step 1: Find active exam session
        ExamSession session = findActiveExamSession(userId);

        log.debug("Found active session id={} for userId={}", session.getId(), userId);

        // Step 2: Find or create answer
        Answer answer = findOrCreateAnswer(session, userId, questionId);

        // Step 3: Update selected answer
        answer.setSelectedAnswer(selectedAnswer);

        // Step 4: Validate correctness
        boolean isCorrect = validateAnswerCorrectness(session, questionId, selectedAnswer);
        answer.setCorrect(isCorrect);

        // Step 5: Save to database
        answerRepository.save(answer);

        log.debug("Answer saved questionId={} correct={}", questionId, isCorrect);
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
     * Find existing answer for question, or create new if not exists
     * Allows users to change answers during exam
     * 
     * @param session    Current exam session
     * @param userId     User ID
     * @param questionId Question ID
     * @return Existing or new Answer entity
     */
    private Answer findOrCreateAnswer(ExamSession session, Long userId, Long questionId) {
        return answerRepository.findByExamSession(session).stream()
                .filter(a -> a.getQuestionId().equals(questionId))
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Creating new answer for questionId={}", questionId);
                    Answer newAnswer = new Answer();
                    newAnswer.setUserId(userId);
                    newAnswer.setQuestionId(questionId);
                    newAnswer.setExamSession(session);
                    return newAnswer;
                });
    }

    /**
     * Validate if selected answer is correct
     * Compares against correct answer from stored questions
     * 
     * @param session        Exam session with stored questions
     * @param questionId     Question ID to validate
     * @param selectedAnswer User's selected answer
     * @return true if correct, false otherwise
     */
    private boolean validateAnswerCorrectness(ExamSession session, Long questionId, String selectedAnswer) {
        try {
            // Deserialize questions from stored JSON
            List<QuizQuestionDTO> questions = objectMapper.readValue(
                    session.getQuestionsJson(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, QuizQuestionDTO.class));

            // Find the question and check if answer is correct
            return questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .anyMatch(q -> {
                        String correctAnswer = q.getAnswers().get(q.getCorrectAnswerIndex());
                        return correctAnswer.trim().equalsIgnoreCase(selectedAnswer.trim());
                    });

        } catch (Exception e) {
            System.err.println("❌ [SaveAnswerUseCase] Failed to validate answer correctness");
            throw new RuntimeException("Could not validate answer", e);
        }
    }
}
