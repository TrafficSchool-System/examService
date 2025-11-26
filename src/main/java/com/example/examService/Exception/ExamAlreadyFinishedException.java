package com.example.examService.Exception;

public class ExamAlreadyFinishedException extends RuntimeException {
    public ExamAlreadyFinishedException(String message) {
        super(message);
    }
}
