package com.example.examService.Controller;

import com.example.examService.Dto.ExamResultDTO;
import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Dto.ExamSessionDTO;
import com.example.examService.Dto.ExamStatsDTO;
import com.example.examService.Dto.SubmitAnswerRequest;
import com.example.examService.Service.ExamServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================================
 * EXAM CONTROLLER - RESTful API
 * ============================================================================
 * 
 * REFACTORED TO FOLLOW REST CONVENTIONS:
 * - Removed redundant /exam from path (/api/exams/exam → /api/exams)
 * - User resource names instead of verbs (active, answers, submission)
 * - Proper HTTP methods (POST for create, GET for read)
 * - Admin endpoints moved to /admin prefix
 * 
 * USER ENDPOINTS:
 * POST /api/exams → Start new exam
 * GET /api/exams/active → Get active exam status
 * POST /api/exams/active/answers → Submit answer to active exam
 * POST /api/exams/active/submission → Finish and submit exam
 * GET /api/exams/latest/result → Get latest exam result
 * GET /api/exams → List all user's exams
 * GET /api/exams/statistics → Get user's exam statistics
 * 
 * ADMIN ENDPOINTS:
 * GET /api/admin/exams/statistics → System-wide exam statistics
 * GET /api/admin/users/{userId}/exams → Get exams for specific user
 */
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamServiceInterface examService;

    public ExamController(ExamServiceInterface examService) {
        this.examService = examService;
    }

    // Helper: Extract userId from Gateway headers
    private Long getUserIdFromRequest(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    // =========================================================================
    // USER ENDPOINTS
    // =========================================================================

    /**
     * START NEW EXAM
     * POST /api/exams
     * 
     * Creates a new exam session for the authenticated user.
     * Requires active subscription.
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamSessionDTO> startExam(
            HttpServletRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromRequest(request);
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        ExamSessionDTO exam = examService.startExam(userId, jwtToken);
        return ResponseEntity.ok(exam);
    }

    /**
     * GET ACTIVE EXAM
     * GET /api/exams/active
     * 
     * Returns the currently active exam for the user.
     * Returns 404 if no active exam exists.
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamSessionDTO> getActiveExam(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamSessionDTO exam = examService.getExamStatus(userId);
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
     */
    @PostMapping("/active/answers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> submitAnswer(
            HttpServletRequest request,
            @RequestBody SubmitAnswerRequest answerRequest) {
        Long userId = getUserIdFromRequest(request);
        examService.saveAnswer(userId, answerRequest.getQuestionId(), answerRequest.getSelectedAnswer());
        return ResponseEntity.ok("Answer saved!");
    }

    /**
     * FINISH EXAM
     * POST /api/exams/active/submission
     * 
     * Finishes and submits the active exam for grading.
     */
    @PostMapping("/active/submission")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> submitExam(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        examService.finishExam(userId);
        return ResponseEntity.ok("Exam submitted!");
    }

    /**
     * GET LATEST EXAM RESULT
     * GET /api/exams/latest/result
     * 
     * Returns the result of the user's most recent completed exam.
     */
    @GetMapping("/latest/result")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamResultDTO> getLatestExamResult(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamResultDTO result = examService.getExamResult(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * LIST USER'S EXAMS
     * GET /api/exams
     * 
     * Returns a list of all exams taken by the user.
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExamResultSummaryDTO>> listUserExams(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<ExamResultSummaryDTO> results = examService.getAllExamResults(userId);
        return ResponseEntity.ok(results);
    }

    /**
     * GET USER STATISTICS
     * GET /api/exams/statistics
     * 
     * Returns statistical data about the user's exam history.
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamStatsDTO> getUserExamStatistics(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamStatsDTO stats = examService.getExamStats(userId);
        return ResponseEntity.ok(stats);
    }

}