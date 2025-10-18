package com.xdai.rag.chatstorage.service.impl;

import com.xdai.rag.chatstorage.dto.ChatMessageRequest;
import com.xdai.rag.chatstorage.dto.ChatMessageResponse;
import com.xdai.rag.chatstorage.entity.ChatMessage;
import com.xdai.rag.chatstorage.entity.ChatSession;
import com.xdai.rag.chatstorage.mapper.ChatMessageMapper;
import com.xdai.rag.chatstorage.repository.ChatMessageRepository;
import com.xdai.rag.chatstorage.repository.ChatSessionRepository;
import com.xdai.rag.chatstorage.service.ChatMessageService;
import com.xdai.rag.common.audit.AuditAction;
import com.xdai.rag.common.audit.Audited;
import com.xdai.rag.common.exception.ChatSessionNotFoundException;
import com.xdai.rag.common.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    @CacheEvict(value = "sessionMessages", key = "#sessionId", allEntries = true)
    @Audited(action = AuditAction.MESSAGE_CREATE)
    @Override
    public ChatMessageResponse addMessage(UUID sessionId, ChatMessageRequest chatMessageRequest) {
        try {
            // Transform to entity and associate with session
            ChatSession chatSession = getChatSessionById(sessionId);
            ChatMessage message = chatMessageMapper.toEntity(chatMessageRequest);
            message.setSession(chatSession);

            // Save the message
            ChatMessage savedMessage = chatMessageRepository.save(message);
            log.info("Message [{}] added to session [{}]", savedMessage.getId(), sessionId);

            return chatMessageMapper.toResponse(savedMessage);

        } catch (InvalidRequestException | ChatSessionNotFoundException e) {
            log.warn("AddMessage validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding message to session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

    @Cacheable(value = "sessionMessages", key = "#sessionId + ':' + #page + ':' + #size")
    @Audited(action = AuditAction.MESSAGES_LIST_BY_SESSION)
    @Override
    public List<ChatMessageResponse> getMessagesBySessionId(UUID sessionId, int page, int size) {
        try {
            // Fetch existing session
            ChatSession session = getChatSessionById(sessionId);

            // Retrieve paginated messages
            PageRequest pageable = PageRequest.of(page, size);
            Page<ChatMessage> messages = chatMessageRepository.findBySessionOrderByCreatedAtAsc(session, pageable);
            log.debug("Fetched {} messages for session [{}]", messages.getTotalElements(), sessionId);

            return chatMessageMapper.toResponseList(messages.getContent());

        } catch (ChatSessionNotFoundException e) {
            log.warn("GetMessagesBySession validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving messages for session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

    @CacheEvict(value = "sessionMessages", allEntries = true)
    @Audited(action = AuditAction.MESSAGE_DELETE)
    @Override
    public void deleteMessageById(UUID messageId) {
        try {
            // Validate existence of message
            if (!chatMessageRepository.existsById(messageId)) {
                throw new ChatSessionNotFoundException("Chat message not found with id: " + messageId);
            }

            // Delete the message
            chatMessageRepository.deleteById(messageId);
            log.info("Deleted message [{}]", messageId);

        } catch (ChatSessionNotFoundException e) {
            log.warn("DeleteMessageById validation error for message [{}]: {}", messageId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting message [{}]: {}", messageId, e.getMessage(), e);
            throw e;
        }
    }

    @CacheEvict(value = "sessionMessages", key = "#sessionId", allEntries = true)
    @Audited(action = AuditAction.MESSAGES_DELETE_BY_SESSION)
    @Override
    public int deleteMessagesBySessionId(UUID sessionId) {
        try {
            // Validate existence of session
            if (!chatSessionRepository.existsById(sessionId)) {
                throw new ChatSessionNotFoundException("Chat session not found with id: " + sessionId);
            }

            // Delete messages associated with the session
            int deletedMessagesCount = chatMessageRepository.deleteBySessionId(sessionId);
            log.info("Deleted {} messages from session [{}]", deletedMessagesCount, sessionId);

            return deletedMessagesCount;

        } catch (ChatSessionNotFoundException e) {
            log.warn("DeleteMessagesBySession validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting messages for session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

    private ChatSession getChatSessionById(UUID sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ChatSessionNotFoundException("Chat session not found with id: " + sessionId));
    }
}
