package com.example.examService.shared.exception;

/**
 * ExamNotFoundException - Thrown when exam or exam session is not found
 * 
 * Use cases:
 * - No active exam session exists for user
 * - No finished exam session found
 * - Exam result not found
 * 
 * HTTP Status: 404 NOT FOUND
 */
public class ExamNotFoundException extends RuntimeException {

    public ExamNotFoundException(String message) {
        super(message);
    }
}
