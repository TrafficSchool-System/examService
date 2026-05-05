package com.example.examService.features.exam.controller;

import com.example.examService.features.exam.dto.ExamResultDTO;
import com.example.examService.features.exam.dto.ExamResultSummaryDTO;
import com.example.examService.features.exam.dto.ExamSessionDTO;
import com.example.examService.features.exam.dto.ExamStatsDTO;
import com.example.examService.features.exam.dto.SubmitAnswerRequest;
import com.example.examService.features.exam.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ExamController - RESTful API for exam operations
 * 
 * Architecture:
 * - Thin controller (no business logic)
 * - Delegates to Use Cases for business operations
 * - Extracts userId from Gateway headers (X-User-Id)
 * 
 * Endpoints (USER role):
 * - POST /api/exams → Start new exam
 * - GET /api/exams/active → Get active exam
 * - POST /api/exams/active/answers → Submit answer
 * - POST /api/exams/active/submission → Finish exam
 * - GET /api/exams/latest/result → Get latest result
 * - GET /api/exams → List all exams
 * - GET /api/exams/statistics → Get statistics
 */
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    // Inject all Use Cases
    private final StartExamUseCase startExamUseCase;
    private final GetActiveExamUseCase getActiveExamUseCase;
    private final SaveAnswerUseCase saveAnswerUseCase;
    private final FinishExamUseCase finishExamUseCase;
    private final GetExamResultUseCase getExamResultUseCase;
    private final ListUserExamsUseCase listUserExamsUseCase;
    private final GetUserStatsUseCase getUserStatsUseCase;

    public ExamController(
            StartExamUseCase startExamUseCase,
            GetActiveExamUseCase getActiveExamUseCase,
            SaveAnswerUseCase saveAnswerUseCase,
            FinishExamUseCase finishExamUseCase,
            GetExamResultUseCase getExamResultUseCase,
            ListUserExamsUseCase listUserExamsUseCase,
            GetUserStatsUseCase getUserStatsUseCase) {
        this.startExamUseCase = startExamUseCase;
        this.getActiveExamUseCase = getActiveExamUseCase;
        this.saveAnswerUseCase = saveAnswerUseCase;
        this.finishExamUseCase = finishExamUseCase;
        this.getExamResultUseCase = getExamResultUseCase;
        this.listUserExamsUseCase = listUserExamsUseCase;
        this.getUserStatsUseCase = getUserStatsUseCase;
    }

    /**
     * Extract userId from Gateway headers
     * 
     * @param request HTTP request with X-User-Id header from Gateway
     * @return userId as Long
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     * START NEW EXAM
     * POST /api/exams
     * 
     * Creates a new exam session for the authenticated user.
     * Closes any active exams and fetches 70 questions from QuizService.
     * 
     * @return ExamSessionDTO with questions and session details
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamSessionDTO> startExam(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamSessionDTO exam = startExamUseCase.execute(userId);
        return ResponseEntity.ok(exam);
    }

    /**
     * GET ACTIVE EXAM
     * GET /api/exams/active
     * 
     * Returns the currently active exam for the user.
     * Includes questions and saved answers.
     * 
     * @return ExamSessionDTO or 404 if no active exam
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamSessionDTO> getActiveExam(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamSessionDTO exam = getActiveExamUseCase.execute(userId);

        if (exam == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(exam);
    }

    /**
     * SUBMIT ANSWER
     * POST /api/exams/active/answers
     * 
     * Submits an answer for a question in the active exam.
     * Validates answer correctness and saves to database.
     * Users can change answers multiple times before finishing.
     * 
     * @param answerRequest Contains questionId and selectedAnswer
     * @return Success message
     */
    @PostMapping("/active/answers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> submitAnswer(
            HttpServletRequest request,
            @RequestBody SubmitAnswerRequest answerRequest) {
        Long userId = getUserIdFromRequest(request);
        saveAnswerUseCase.execute(userId, answerRequest.getQuestionId(), answerRequest.getSelectedAnswer());
        return ResponseEntity.ok("Answer saved!");
    }

    /**
     * FINISH EXAM
     * POST /api/exams/active/submission
     * 
     * Finishes and submits the active exam for grading.
     * Calculates score, determines pass/fail (70%), creates Result entity.
     * 
     * @return Success message
     */
    @PostMapping("/active/submission")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> submitExam(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        finishExamUseCase.execute(userId);
        return ResponseEntity.ok("Exam submitted!");
    }

    /**
     * GET LATEST EXAM RESULT
     * GET /api/exams/latest/result
     * 
     * Returns the result of the user's most recent completed exam.
     * Includes detailed breakdown with all questions and user answers.
     * 
     * @return ExamResultDTO with score, questions, answers, time taken
     */
    @GetMapping("/latest/result")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamResultDTO> getLatestExamResult(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamResultDTO result = getExamResultUseCase.execute(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * LIST USER'S EXAMS
     * GET /api/exams
     * 
     * Returns a list of all exams taken by the user.
     * Each entry includes summary data (score, pass/fail, date).
     * 
     * @return List of ExamResultSummaryDTO
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExamResultSummaryDTO>> listUserExams(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<ExamResultSummaryDTO> results = listUserExamsUseCase.execute(userId);
        return ResponseEntity.ok(results);
    }

    /**
     * GET USER STATISTICS
     * GET /api/exams/statistics
     * 
     * Returns statistical data about the user's exam history.
     * Includes total/passed/failed, average percentage, streaks, readiness.
     * 
     * @return ExamStatsDTO with comprehensive statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamStatsDTO> getUserExamStatistics(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamStatsDTO stats = getUserStatsUseCase.execute(userId);
        return ResponseEntity.ok(stats);
    }
}
