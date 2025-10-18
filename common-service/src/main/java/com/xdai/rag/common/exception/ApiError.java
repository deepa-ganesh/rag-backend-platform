package com.xdai.rag.common.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class ApiError {
    private UUID errorId;
    private HttpStatus status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}
