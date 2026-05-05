package com.example.examService.shared.exception;

/**
 * JsonParseException - Thrown when JSON serialization/deserialization fails
 * 
 * Use cases:
 * - Failed to serialize questions to JSON for storage
 * - Failed to deserialize questions from JSON
 * - Corrupted questionsJson data in database
 * 
 * HTTP Status: 500 INTERNAL SERVER ERROR
 */
public class JsonParseException extends RuntimeException {

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
