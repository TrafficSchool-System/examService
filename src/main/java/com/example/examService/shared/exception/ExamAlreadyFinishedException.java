package com.example.examService.shared.exception;

/**
 * ExamAlreadyFinishedException - Thrown when trying to modify a finished exam
 * 
 * Use cases:
 * - Attempting to submit answer to finished exam
 * - Attempting to finish already finished exam
 * 
 * HTTP Status: 400 BAD REQUEST
 */
public class ExamAlreadyFinishedException extends RuntimeException {

    public ExamAlreadyFinishedException(String message) {
        super(message);
    }
}
