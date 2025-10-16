package com.xdai.ragchatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating a new chat message")
public class ChatMessageRequest {

    @Schema(description = "Sender of the message", example = "USER")
    private String sender;

    @Schema(description = "Message content", example = "Explain quantum computing in simple terms.")
    private String content;

    @Schema(description = "Optional retrieved context text", example = "Quantum computing uses qubits instead of bits.")
    private String context;

    @Schema(description = "Name of the source document", example = "Quantum Basics")
    private String sourceName;

    @Schema(description = "Type of the source (pdf, web, etc.)", example = "web_article")
    private String sourceType;
}
