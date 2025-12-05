package com.example.examService.Controller;

import com.example.examService.Dto.ExamResultDTO;
import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Dto.ExamSessionDTO;
import com.example.examService.Dto.ExamStatsDTO;
import com.example.examService.Service.ExamServiceInterface;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamServiceInterface examService;

    // Konstruktor-injektion: Spring skickar in vår ExamService automatiskt
    public ExamController(ExamServiceInterface examService) {
        this.examService = examService;
    }

    // === Starta ett nytt prov ===
    // Här startar vi en ny exam-session för en användare.
    // Vi skickar med userId som query-parameter (?userId=123).
    @PostMapping("/start")
    public ResponseEntity<ExamSessionDTO> startExam(@RequestParam Long userId) {
        ExamSessionDTO exam = examService.startExam(userId);
        return ResponseEntity.ok(exam);
    }

    // === Hämta status för pågående prov ===
    // Hämtar exam-sessionen för en användare om den inte är avslutad.
    @GetMapping("/status")
    public ResponseEntity<ExamSessionDTO> getExamStatus(@RequestParam Long userId) {
        ExamSessionDTO exam = examService.getExamStatus(userId);
        if (exam == null) {
            return ResponseEntity.notFound().build(); // returnerar 404 om ingen session finns
        }
        return ResponseEntity.ok(exam);
    }

    // === Spara svar ===
    // Här anropar vi service-metoden saveAnswer som sparar användarens svar i
    // databasen.
    // Vi skickar med userId, questionId och selectedAnswer som query-parametrar.
    @PostMapping("/answer")
    public ResponseEntity<String> saveAnswer(
            @RequestParam Long userId,
            @RequestParam Long questionId,
            @RequestParam String selectedAnswer) {
        examService.saveAnswer(userId, questionId, selectedAnswer);
        return ResponseEntity.ok("Svar sparat!");
    }

    // === Avsluta prov ===
    // När användaren är klar med provet anropar vi finishExam.
    // Den räknar rätt/fel och sparar resultatet i databasen.
    @PostMapping("/finish")
    public ResponseEntity<String> finishExam(@RequestParam Long userId) {
        examService.finishExam(userId);
        return ResponseEntity.ok("Prov avslutat!");
    }

    // === Hämta resultat ===
    // Hämtar resultatet för användarens senaste avslutade prov
    @GetMapping("/result")
    public ResponseEntity<ExamResultDTO> getExamResult(@RequestParam Long userId) {
        ExamResultDTO result = examService.getExamResult(userId);
        return ResponseEntity.ok(result);
    }

    // === Hämta alla provresultat för en användare 
    @GetMapping("/results")
    public ResponseEntity<List<ExamResultSummaryDTO>> getAllResults(@RequestParam Long userId) {
        List<ExamResultSummaryDTO> results = examService.getAllExamResults(userId); 
        return ResponseEntity.ok(results); 
    }

    // === Hämta statestik för en användar ===
    @GetMapping("/stats")
    public ResponseEntity<ExamStatsDTO> getStats (@RequestParam Long userId) {
        ExamStatsDTO stats = examService.getExamStats(userId); 
        return ResponseEntity.ok(stats); 
    }
}