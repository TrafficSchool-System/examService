package com.example.examService.Service;

import com.example.examService.Dto.ExamSessionDTO;

public interface ExamServiceInterface {
    ExamSessionDTO startExam(Long userId);
    ExamSessionDTO getExamStatus(Long userId);
    void saveAnswer(Long userId, Long questionId, String selectedAnswer);
    void finishExam(Long userId);
}