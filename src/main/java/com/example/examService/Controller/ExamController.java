package com.example.examService.Controller;

import com.example.examService.Dto.ExamResultDTO;
import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Dto.ExamSessionDTO;
import com.example.examService.Dto.ExamStatsDTO;
import com.example.examService.Dto.SubmitAnswerRequest;
import com.example.examService.Service.ExamServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamServiceInterface examService;

    public ExamController(ExamServiceInterface examService) {
        this.examService = examService;
    }

    // Hämta userId från JWT token (satt av JwtAuthenticationFilter)
    private Long getUserIdFromRequest(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamSessionDTO> startExam(
            HttpServletRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = getUserIdFromRequest(request);
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        ExamSessionDTO exam = examService.startExam(userId, jwtToken);
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamSessionDTO> getExamStatus(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamSessionDTO exam = examService.getExamStatus(userId);
        if (exam == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exam);
    }

    @PostMapping("/answer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> saveAnswer(
            HttpServletRequest request,
            @RequestBody SubmitAnswerRequest answerRequest) {
        Long userId = getUserIdFromRequest(request);
        examService.saveAnswer(userId, answerRequest.getQuestionId(), answerRequest.getSelectedAnswer());
        return ResponseEntity.ok("Svar sparat!");
    }

    @PostMapping("/finish")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> finishExam(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        examService.finishExam(userId);
        return ResponseEntity.ok("Prov avslutat!");
    }

    @GetMapping("/result")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamResultDTO> getExamResult(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamResultDTO result = examService.getExamResult(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ExamResultSummaryDTO>> getAllResults(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<ExamResultSummaryDTO> results = examService.getAllExamResults(userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ExamStatsDTO> getStats(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        ExamStatsDTO stats = examService.getExamStats(userId);
        return ResponseEntity.ok(stats);
    }

    
}