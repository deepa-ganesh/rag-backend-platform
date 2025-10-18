package com.xdai.rag.chatstorage.service;

import com.xdai.rag.chatstorage.dto.ChatMessageRequest;
import com.xdai.rag.chatstorage.dto.ChatMessageResponse;

import java.util.List;
import java.util.UUID;

public interface ChatMessageService {

    ChatMessageResponse addMessage(UUID sessionId, ChatMessageRequest request);

    List<ChatMessageResponse> getMessagesBySessionId(UUID sessionId, int page, int size);

    void deleteMessageById(UUID messageId);

    int deleteMessagesBySessionId(UUID sessionId);
}
