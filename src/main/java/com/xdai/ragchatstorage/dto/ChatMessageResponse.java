package com.xdai.ragchatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response object representing a chat message with optional RAG metadata")
public class ChatMessageResponse {

    @Schema(example = "d4b0f6e3-1234-4a6b-b5c2-ef9e7fcd990b")
    private UUID id;

    private String sender;
    private String content;
    private String context;
    private String sourceName;
    private String sourceType;
    private LocalDateTime createdAt;
}
