package com.xdai.rag.chatstorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xdai.rag.chatstorage.common.Sender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object representing a chat message with optional RAG metadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ChatMessageResponse {

    @Schema(example = "d4b0f6e3-1234-4a6b-b5c2-ef9e7fcd990b")
    private UUID id;

    private Sender sender;
    private String content;
    private String context;
    private String sourceName;
    private String sourceType;
    private Timestamp createdAt;
}
