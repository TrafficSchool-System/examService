package com.example.examService.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Service.ExamServiceInterface;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/exams")
public class AdminExamController {

    private final ExamServiceInterface examService;

    public AdminExamController(ExamServiceInterface examService) {
        this.examService = examService;
    }

    // ADMIN endpoint - kan se alla användares resultat
    @GetMapping("/admin/results/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExamResultSummaryDTO>> getResultsByAdmin(@PathVariable Long userId) {
        List<ExamResultSummaryDTO> results = examService.getAllExamResults(userId);
        return ResponseEntity.ok(results);
    }

     // ADMIN endpoint - hämta antal aktiva och avslutade prov
    @GetMapping("/counts")
    public ResponseEntity<Map<String, Integer>> getExamCounts() {
        return ResponseEntity.ok(examService.getExamCounts());
    }

}
