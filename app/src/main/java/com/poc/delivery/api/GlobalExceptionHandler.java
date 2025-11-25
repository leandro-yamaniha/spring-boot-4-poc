package com.poc.delivery.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.poc.delivery.common.logging.LogEvent;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception ex) {
        LOGGER.error("[{}] Erro inesperado no processamento da requisicao", LogEvent.ORDER_UNEXPECTED_ERROR.code(), ex);
        ApiError error = new ApiError("INTERNAL_ERROR", "An unexpected error occurred");
        ApiErrorResponse body = new ApiErrorResponse(error);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.warn("[{}] Erro de validacao de negocio: {}", LogEvent.ORDER_VALIDATION_FAILED.code(), ex.getMessage());
        ApiError error = new ApiError("BAD_REQUEST", ex.getMessage());
        ApiErrorResponse body = new ApiErrorResponse(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Validation failed");
        LOGGER.warn("[{}] Erro de validacao de entrada: {}", LogEvent.ORDER_VALIDATION_FAILED.code(), message);
        ApiError error = new ApiError("BAD_REQUEST", message);
        ApiErrorResponse body = new ApiErrorResponse(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    public record ApiError(String code, String message) {
    }

    public record ApiErrorResponse(ApiError error) {
    }
}
