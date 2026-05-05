package com.example.examService.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling
 * 
 * Purpose:
 * - Convert exceptions to consistent JSON error responses
 * - Log errors for debugging
 * - Return appropriate HTTP status codes
 * 
 * Handled Exceptions:
 * - ExamNotFoundException → 404 NOT FOUND
 * - ExamAlreadyFinishedException → 400 BAD REQUEST
 * - JsonParseException → 500 INTERNAL SERVER ERROR
 * - Exception → 500 INTERNAL SERVER ERROR (catch-all)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Build error response map
     */
    private Map<String, Object> buildError(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    /**
     * Handle ExamNotFoundException
     * Returns 404 NOT FOUND
     */
    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleExamNotFound(ExamNotFoundException ex) {
        return new ResponseEntity<>(
                buildError("Exam not found", ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Handle ExamAlreadyFinishedException
     * Returns 400 BAD REQUEST
     */
    @ExceptionHandler(ExamAlreadyFinishedException.class)
    public ResponseEntity<Map<String, Object>> handleExamAlreadyFinished(ExamAlreadyFinishedException ex) {
        return new ResponseEntity<>(
                buildError("Exam already finished", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle JsonParseException
     * Returns 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParse(JsonParseException ex) {
        return new ResponseEntity<>(
                buildError("JSON Parse Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions
     * Returns 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherException(Exception ex) {
        // Let Spring Security handle its own exceptions
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            throw (org.springframework.security.access.AccessDeniedException) ex;
        }

        return new ResponseEntity<>(
                buildError("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
