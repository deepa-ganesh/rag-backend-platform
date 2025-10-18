package com.xdai.rag.chatstorage.service;

import com.xdai.rag.chatstorage.dto.ChatSessionRequest;
import com.xdai.rag.chatstorage.dto.ChatSessionResponse;

import java.util.List;
import java.util.UUID;

public interface ChatSessionService {

    ChatSessionResponse createSession(ChatSessionRequest chatSessionRequest);

    void deleteSession(UUID sessionId);

    ChatSessionResponse renameSession(UUID sessionId, ChatSessionRequest chatSessionRequest);

    ChatSessionResponse markAsFavorite(UUID sessionId);

    ChatSessionResponse unmarkAsFavorite(UUID sessionId);

    List<ChatSessionResponse> getAllSessions();
}
