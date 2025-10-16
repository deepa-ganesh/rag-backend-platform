package com.xdai.ragchatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for creating or renaming a chat session")
public class ChatSessionRequest {

    @Schema(description = "Name of the chat session", example = "Green Hydrogen Research Discussion")
    private String name;
}
