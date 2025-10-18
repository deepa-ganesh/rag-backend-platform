package com.xdai.rag.chatstorage.controller;

import com.xdai.rag.chatstorage.dto.ChatSessionRequest;
import com.xdai.rag.chatstorage.dto.ChatSessionResponse;
import com.xdai.rag.chatstorage.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing chat sessions.
 *
 * @author Deepa Ganesh
 */
@Tag(name = "Chat Sessions", description = "APIs for managing chat sessions")
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@RestController
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    /**
     * Creates a new chat session.
     *
     * @param chatSessionRequest The ChatSessionRequest object containing the session details.
     * @return A ResponseEntity containing the created ChatSessionResponse object.
     */
    @Operation(
            summary = "Create a new chat session",
            description = "Creates a new chat session with the provided name.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @PostMapping(value = "/", headers = "Content-Type=" + MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatSessionResponse> createSession(@Valid @RequestBody ChatSessionRequest chatSessionRequest) {

        ChatSessionResponse chatSessionResponse = chatSessionService.createSession(chatSessionRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatSessionResponse);
    }


    /**
     * Renames an existing chat session.
     *
     * @param sessionId          The UUID of the chat session to be renamed.
     * @param chatSessionRequest The ChatSessionRequest object containing the new name.
     * @return A ResponseEntity containing the updated ChatSessionResponse object.
     */
    @Operation(
            summary = "Rename an existing chat session",
            description = "Renames the chat session with the specified session ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @PatchMapping(value = "/{sessionId}/rename", headers = "Content-Type=" + MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatSessionResponse> renameSession(@PathVariable("sessionId") UUID sessionId,
                                                             @Valid @RequestBody ChatSessionRequest chatSessionRequest) {

        return ResponseEntity.ok(chatSessionService.renameSession(sessionId, chatSessionRequest));
    }


    /**
     * Marks or unmarks a chat session as favorite.
     *
     * @param sessionId The UUID of the chat session to be marked/unmarked.
     * @param favorite  A boolean indicating whether to mark (true) or unmark (false) the session as favorite.
     * @return A ResponseEntity containing the updated ChatSessionResponse object.
     */
    @Operation(
            summary = "Mark or unmark a chat session as favorite",
            description = "Marks or unmarks the chat session with the specified session ID as favorite.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @PatchMapping(value = "/{sessionId}/favorite", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatSessionResponse> markUnmarkAsFavorite(@PathVariable("sessionId") UUID sessionId,
                                                                    @RequestParam(defaultValue = "true") Boolean favorite) {
        ChatSessionResponse chatSessionResponse;

        if (favorite) {
            chatSessionResponse = chatSessionService.markAsFavorite(sessionId);
        } else {
            chatSessionResponse = chatSessionService.unmarkAsFavorite(sessionId);
        }

        return ResponseEntity.ok(chatSessionResponse);
    }


    /**
     * Deletes a chat session and its associated messages.
     *
     * @param sessionId The UUID of the chat session to be deleted.
     * @return A ResponseEntity with no content.
     */
    @Operation(
            summary = "Delete a chat session and its messages",
            description = "Deletes the chat session with the specified session ID along with all its associated messages.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable("sessionId") UUID sessionId) {

        chatSessionService.deleteSession(sessionId);

        return ResponseEntity.noContent().build();
    }


    /**
     * Lists all chat sessions.
     *
     * @return A ResponseEntity containing a list of ChatSessionResponse objects.
     */
    @Operation(
            summary = "List chat sessions (paginated, optional favorite filter)",
            description = "Retrieves a list of all chat sessions. Optionally, filter by favorite status.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ChatSessionResponse>> listSessions() {

        return ResponseEntity.ok(chatSessionService.getAllSessions());
    }

    /**
     * Lists session by session ID.
     *
     * @param sessionId The UUID of the chat session to retrieve.
     * @return A ResponseEntity containing the ChatSessionResponse object.
     */
    @Operation(
            summary = "Get chat session by ID",
            description = "Retrieves the chat session with the specified session ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")}
    )
    @GetMapping(value = "/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatSessionResponse> getSessionById(@PathVariable("sessionId") UUID sessionId) {

        return ResponseEntity.ok(chatSessionService.getSessionById(sessionId));
    }
}
