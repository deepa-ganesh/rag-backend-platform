package com.xdai.ragchatstorage.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApiError {
    private UUID errorId;
    private HttpStatus status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path; // optional – filled by filter or controller advice
}
