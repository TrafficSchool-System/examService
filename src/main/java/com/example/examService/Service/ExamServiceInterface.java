package com.example.examService.Service;

import java.util.List;

import com.example.examService.Dto.ExamResultDTO;
import com.example.examService.Dto.ExamResultSummaryDTO;
import com.example.examService.Dto.ExamSessionDTO;

public interface ExamServiceInterface {
    ExamSessionDTO startExam(Long userId);

    ExamSessionDTO getExamStatus(Long userId);

    void saveAnswer(Long userId, Long questionId, String selectedAnswer);

    void finishExam(Long userId);

    ExamResultDTO getExamResult(Long userId);

    List<ExamResultSummaryDTO> getAllExamResults(Long userId); 
}