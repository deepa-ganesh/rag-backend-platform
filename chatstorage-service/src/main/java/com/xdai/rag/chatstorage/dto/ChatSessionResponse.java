package com.xdai.rag.chatstorage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object representing a chat session")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ChatSessionResponse {

    @Schema(example = "9a19a550-6c44-40b3-bd7a-9d018f62d2f1")
    private UUID id;

    @Schema(description = "Session title", example = "Green Hydrogen Research Discussion")
    private String name;

    @Schema(description = "Whether this session is marked as favorite", example = "false")
    private Boolean favorite;

    @Schema(description = "Session creation timestamp", example = "2025-10-16T21:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createdAt;

    @Schema(description = "Last update timestamp", example = "2025-10-16T21:15:32")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp updatedAt;
}
