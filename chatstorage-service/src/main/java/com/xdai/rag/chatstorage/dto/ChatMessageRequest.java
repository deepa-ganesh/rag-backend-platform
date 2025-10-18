package com.xdai.rag.chatstorage.dto;

import com.xdai.rag.chatstorage.common.Sender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Request payload for creating a new chat message")
@Data
public class ChatMessageRequest {

    @Schema(description = "Sender of the message", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Sender cannot be blank")
    private Sender sender;

    @Schema(description = "Message content", example = "Hello! How is the weather today?", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Content cannot be blank")
    private String content;

    @Schema(description = "Optional retrieved context text", example = "Discussion about today's weather forecast")
    private String context;

    @Schema(description = "Name of the source document", example = "Web Article on Weather")
    private String sourceName;

    @Schema(description = "Type of the source (pdf, web, etc.)", example = "web_article")
    private String sourceType;
}
