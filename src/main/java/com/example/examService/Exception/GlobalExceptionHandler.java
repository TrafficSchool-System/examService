package com.example.examService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildError(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(ExamNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleExamNotFound(ExamNotFoundException ex) {
        return new ResponseEntity<>(
            buildError("Exam not found", ex.getMessage(), HttpStatus.NOT_FOUND.value()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ExamAlreadyFinishedException.class)
    public ResponseEntity<Map<String, Object>> handleExamAlreadyFinished(ExamAlreadyFinishedException ex) {
        return new ResponseEntity<>(
            buildError("Exam already finished", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParse(JsonParseException ex) {
        return new ResponseEntity<>(
            buildError("JSON Parse Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherException(Exception ex) {
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            // Låt Spring Security ta hand om detta
            throw (org.springframework.security.access.AccessDeniedException) ex;
        }

        return new ResponseEntity<>(
            buildError("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}