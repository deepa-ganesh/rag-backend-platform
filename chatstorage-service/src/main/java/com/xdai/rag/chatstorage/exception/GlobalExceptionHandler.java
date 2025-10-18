package com.xdai.rag.chatstorage.exception;

import com.xdai.rag.common.exception.ApiError;
import com.xdai.rag.common.exception.ChatMessageNotFoundException;
import com.xdai.rag.common.exception.ChatSessionNotFoundException;
import com.xdai.rag.common.exception.InvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ApiError buildError(HttpStatus status, String code, String message, HttpServletRequest request) {

        return ApiError.builder()
                .errorId(UUID.randomUUID())
                .status(status)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(ChatSessionNotFoundException.class)
    public ResponseEntity<ApiError> handleChatSessionNotFoundException(ChatSessionNotFoundException ex, HttpServletRequest request) {

        ApiError error = buildError(HttpStatus.NOT_FOUND, "SESSION_NOT_FOUND", ex.getMessage(), request);
        log.warn("Chat Session not found: {}", error);

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ChatMessageNotFoundException.class)
    public ResponseEntity<ApiError> handleChatMessageNotFoundException(ChatMessageNotFoundException ex, HttpServletRequest request) {

        ApiError error = buildError(HttpStatus.NOT_FOUND, "MESSAGE_NOT_FOUND", ex.getMessage(), request);
        log.warn("Message not found: {}", error);

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleIInvalidRequestException(InvalidRequestException ex, HttpServletRequest request) {

        ApiError error = buildError(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex.getMessage(), request);
        log.info("Invalid request: {}", error);

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ApiError error = buildError(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, request);
        log.info("Validation failed: {}", error);

        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {

        ApiError error = buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", request);
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
