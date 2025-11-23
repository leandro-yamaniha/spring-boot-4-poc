package com.poc.delivery.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex) {
        ApiError error = new ApiError("INTERNAL_ERROR", "An unexpected error occurred");
        ApiErrorResponse body = new ApiErrorResponse(error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    public record ApiError(String code, String message) {
    }

    public record ApiErrorResponse(ApiError error) {
    }
}
