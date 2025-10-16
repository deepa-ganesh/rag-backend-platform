package com.xdai.ragchatstorage.service;

import com.xdai.ragchatstorage.dto.ChatMessageRequest;
import com.xdai.ragchatstorage.dto.ChatMessageResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ChatMessageService {
    ChatMessageResponse addMessage(UUID sessionId, ChatMessageRequest request);

    Page<ChatMessageResponse> getMessagesBySessionId(UUID sessionId, int page, int size);

    void deleteMessageById(UUID messageId);

    int deleteMessagesBySessionId(UUID sessionId);
}
