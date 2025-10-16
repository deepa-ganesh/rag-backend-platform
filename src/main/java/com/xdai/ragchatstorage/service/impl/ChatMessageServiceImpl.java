package com.xdai.ragchatstorage.service.impl;

import com.xdai.ragchatstorage.audit.AuditAction;
import com.xdai.ragchatstorage.audit.Audited;
import com.xdai.ragchatstorage.dto.ChatMessageRequest;
import com.xdai.ragchatstorage.dto.ChatMessageResponse;
import com.xdai.ragchatstorage.entity.ChatMessage;
import com.xdai.ragchatstorage.entity.ChatSession;
import com.xdai.ragchatstorage.exception.ChatSessionNotFoundException;
import com.xdai.ragchatstorage.exception.InvalidRequestException;
import com.xdai.ragchatstorage.mapper.ChatMessageMapper;
import com.xdai.ragchatstorage.repository.ChatMessageRepository;
import com.xdai.ragchatstorage.repository.ChatSessionRepository;
import com.xdai.ragchatstorage.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Audited(action = AuditAction.MESSAGE_CREATE)
    @Override
    public ChatMessageResponse addMessage(UUID sessionId, ChatMessageRequest request) {
        if (request.getSender() == null || request.getSender().isBlank()) {
            throw new InvalidRequestException("Sender is required");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new InvalidRequestException("Message content cannot be empty");
        }

        ChatSession chatSession = getChatSessionById(sessionId);

        ChatMessage message = chatMessageMapper.toEntity(request);
        message.setChatSession(chatSession);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("Added new message [{}] to session [{}]", savedMessage.getId(), sessionId);

        return chatMessageMapper.toResponse(savedMessage);
    }

    @Audited(action = AuditAction.MESSAGES_LIST_BY_SESSION)
    @Override
    public Page<ChatMessageResponse> getMessagesBySessionId(UUID sessionId, int page, int size) {
        ChatSession session = getChatSessionById(sessionId);

        PageRequest pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findBySessionOrderByCreatedAtAsc(session, pageable);

        return messages.map(chatMessageMapper::toResponse);
    }

    @Audited(action = AuditAction.MESSAGE_DELETE)
    @Override
    public void deleteMessageById(UUID messageId) {
        if (!chatMessageRepository.existsById(messageId)) {
            throw new ChatSessionNotFoundException("Chat message not found with id: " + messageId);
        }

        chatMessageRepository.deleteById(messageId);
        log.info("Deleted message from session [{}]", messageId);
    }

    @Audited(action = AuditAction.MESSAGES_DELETE_BY_SESSION)
    @Override
    public int deleteMessagesBySessionId(UUID sessionId) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new ChatSessionNotFoundException("Chat session not found with id: " + sessionId);
        }

        int deletedCount = chatMessageRepository.deleteBySessionId(sessionId);
        log.info("Deleted {} messages from session [{}]", deletedCount, sessionId);

        return deletedCount;
    }

    private ChatSession getChatSessionById(UUID sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ChatSessionNotFoundException("Chat session not found with id: " + sessionId));
    }
}
