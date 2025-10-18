package com.xdai.rag.chatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Request payload for creating or renaming a chat session")
@Data
public class ChatSessionRequest {

    @Schema(description = "Name of the chat session", example = "Weather Chat", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Session name must not be blank")
    private String name;
}
