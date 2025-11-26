package com.example.examService.Controller;

import com.example.examService.Dto.ExamSessionDTO;
import com.example.examService.Service.ExamServiceInterface;
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
    // Här anropar vi service-metoden saveAnswer som sparar användarens svar i databasen.
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
}