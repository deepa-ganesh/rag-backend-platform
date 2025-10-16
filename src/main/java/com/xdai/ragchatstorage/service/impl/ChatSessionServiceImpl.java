package com.xdai.ragchatstorage.service.impl;

import com.xdai.ragchatstorage.audit.AuditAction;
import com.xdai.ragchatstorage.audit.Audited;
import com.xdai.ragchatstorage.dto.ChatSessionRequest;
import com.xdai.ragchatstorage.dto.ChatSessionResponse;
import com.xdai.ragchatstorage.entity.ChatSession;
import com.xdai.ragchatstorage.exception.ChatSessionNotFoundException;
import com.xdai.ragchatstorage.exception.InvalidRequestException;
import com.xdai.ragchatstorage.mapper.ChatSessionMapper;
import com.xdai.ragchatstorage.repository.ChatMessageRepository;
import com.xdai.ragchatstorage.repository.ChatSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatSessionServiceImpl extends ChatSession {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionMapper chatSessionMapper;

    @Audited(action = AuditAction.SESSIONS_LIST, includeResponse = false)
    @Override
    public Page<ChatSessionResponse> getAllSessions(int page, int size, Boolean favorite) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<ChatSession> sessions = (favorite != null)
                ? chatSessionRepository.findByFavorite(favorite, pageable)
                : chatSessionRepository.findAll(pageable);

        return sessions.map(chatSessionMapper::toResponse);
    }

    @Audited(action = AuditAction.SESSION_CREATE)
    @Override
    public ChatSessionResponse createSession(ChatSessionRequest chatSessionRequest) {
        ChatSession chatSession = chatSessionMapper.toEntity(chatSessionRequest);
        ChatSession savedSession = chatSessionRepository.save(chatSession);

        return chatSessionMapper.toResponse(savedSession);
    }

    @Audited(action = AuditAction.SESSION_DELETE)
    @Override
    public void deleteSession(UUID sessionId) {
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new ChatSessionNotFoundException("Chat session not found with id: " + sessionId);
        }

        // 1. Delete all messages in this session
        chatMessageRepository.deleteBySessionId(sessionId);

        // 2. Delete the session itself
        chatSessionRepository.deleteById(sessionId);
    }

    @Audited(action = AuditAction.SESSION_RENAME)
    @Override
    public ChatSessionResponse renameSession(UUID sessionId, String newSessionName) {
        ChatSession chatSession = getChatSessionById(sessionId);

        if (StringUtils.isBlank(newSessionName)) {
            throw new InvalidRequestException("Session name cannot be empty");
        }

        chatSession.setName(newSessionName);
        return chatSessionMapper.toResponse(chatSessionRepository.save(chatSession));
    }

    @Audited(action = AuditAction.SESSION_MARK_FAVORITE)
    @Override
    public ChatSessionResponse markAsFavorite(UUID sessionId) {
        ChatSession chatSession = getChatSessionById(sessionId);

        chatSession.setFavorite(!chatSession.getFavorite());
        return chatSessionMapper.toResponse(chatSessionRepository.save(chatSession));
    }

    private ChatSession getChatSessionById(UUID sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ChatSessionNotFoundException("Chat session not found with id: " + sessionId));
    }
}

