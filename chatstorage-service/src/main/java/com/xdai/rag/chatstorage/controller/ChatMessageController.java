package com.xdai.rag.chatstorage.controller;

import com.xdai.rag.chatstorage.dto.ChatMessageRequest;
import com.xdai.rag.chatstorage.dto.ChatMessageResponse;
import com.xdai.rag.chatstorage.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing chat messages within chat sessions.
 *
 * @author Deepa Ganesh
 */
@Tag(name = "Chat Messages", description = "APIs for managing chat messages")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * Adds a new message to an existing chat session.
     *
     * @param sessionId The UUID of the chat session to which the message is to be added.
     * @param request   The ChatMessageRequest object containing the message details.
     * @return A ResponseEntity containing the created ChatMessageResponse object.
     */
    @Operation(
            summary = "Add a message to an existing chat session",
            description = "Adds a new user or AI message to the chat session with the specified session ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @PostMapping(value = "/sessions/{sessionId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatMessageResponse> addMessage(@PathVariable("sessionId") UUID sessionId,
                                                          @Valid @RequestBody ChatMessageRequest request) {

        ChatMessageResponse chatMessageResponse = chatMessageService.addMessage(sessionId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessageResponse);
    }


    /**
     * Retrieves all messages associated with a specific session ID.
     *
     * @param sessionId The UUID of the chat session whose messages are to be retrieved.
     * @param page      The page number to retrieve (default is 0).
     * @param size      The number of messages per page (default is 20).
     * @return A ResponseEntity containing a list of ChatMessageResponse objects.
     */
    @Operation(
            summary = "Retrieve all messages of a session",
            description = "Fetches all chat messages for the specified session ID with pagination support.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @GetMapping(value = "/sessions/{sessionId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable("sessionId") UUID sessionId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(chatMessageService.getMessagesBySessionId(sessionId, page, size));
    }


    /**
     * Deletes all messages associated with a specific session ID.
     *
     * @param sessionId The UUID of the chat session whose messages are to be deleted.
     * @return A ResponseEntity containing the count of deleted messages.
     */
    @Operation(
            summary = "Delete all messages in a session",
            description = "Deletes all chat messages associated with the specified session ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @DeleteMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<Integer> deleteMessagesBySession(@PathVariable("sessionId") UUID sessionId) {

        int deletedMessagedCount = chatMessageService.deleteMessagesBySessionId(sessionId);

        return ResponseEntity.ok(deletedMessagedCount);
    }


    /**
     * Deletes a single message by its ID.
     *
     * @param messageId The UUID of the message to be deleted.
     * @return A ResponseEntity with no content.
     */
    @Operation(
            summary = "Delete a single message by ID",
            description = "Deletes a specific chat message identified by its message ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {

        chatMessageService.deleteMessageById(messageId);

        return ResponseEntity.noContent().build();
    }
}
