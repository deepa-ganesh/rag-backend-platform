package com.xdai.rag.chatstorage.service;

import com.xdai.rag.chatstorage.dto.ChatSessionRequest;
import com.xdai.rag.chatstorage.dto.ChatSessionResponse;
import com.xdai.rag.chatstorage.entity.ChatSession;
import com.xdai.rag.chatstorage.mapper.ChatSessionMapper;
import com.xdai.rag.chatstorage.repository.ChatMessageRepository;
import com.xdai.rag.chatstorage.repository.ChatSessionRepository;
import com.xdai.rag.common.exception.ChatSessionNotFoundException;
import com.xdai.rag.chatstorage.service.impl.ChatSessionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceImplTest {

    @Mock private ChatSessionRepository chatSessionRepository;
    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private ChatSessionMapper chatSessionMapper;

    @InjectMocks private ChatSessionServiceImpl chatSessionService;

    private UUID sessionId;
    private ChatSession chatSession;
    private ChatSessionRequest request;
    private ChatSessionResponse response;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();

        chatSession = new ChatSession();
        chatSession.setId(sessionId);
        chatSession.setName("Test Session");

        request = new ChatSessionRequest();
        request.setName("Updated Name");

        response = new ChatSessionResponse();
        response.setId(sessionId);
        response.setName("Updated Name");
        response.setFavorite(true);
    }

    @Test
    void getAllSessionsSuccessTest() {
        when(chatSessionRepository.findAll(any(Sort.class))).thenReturn(List.of(chatSession));
        when(chatSessionMapper.toResponseList(anyList())).thenReturn(List.of(response));

        List<ChatSessionResponse> sessions = chatSessionService.getAllSessions();

        assertEquals(1, sessions.size());
        verify(chatSessionRepository).findAll(any(Sort.class));
    }

    @Test
    void getSessionByIdSuccessTest() {
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(response);

        ChatSessionResponse result = chatSessionService.getSessionById(sessionId);

        assertEquals(response, result);
    }

    @Test
    void getSessionByIdNotFoundTest() {
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        assertThrows(ChatSessionNotFoundException.class, () -> chatSessionService.getSessionById(sessionId));
    }

    @Test
    void createSessionSuccessTest() {
        when(chatSessionMapper.toEntity(request)).thenReturn(chatSession);
        when(chatSessionRepository.save(chatSession)).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(response);

        ChatSessionResponse result = chatSessionService.createSession(request);

        assertEquals(response, result);
        verify(chatSessionRepository).save(chatSession);
    }

    @Test
    void deleteSessionSuccessTest() {
        when(chatSessionRepository.existsById(sessionId)).thenReturn(true);
        when(chatMessageRepository.deleteBySessionId(sessionId)).thenReturn(2);

        chatSessionService.deleteSession(sessionId);

        verify(chatSessionRepository).deleteById(sessionId);
    }

    @Test
    void renameSessionSuccessTest() {
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(chatSession)).thenReturn(response);

        ChatSessionResponse result = chatSessionService.renameSession(sessionId, request);

        assertEquals(response, result);
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    @Test
    void markAsFavoriteSuccessTest() {
        chatSession.setFavorite(false);
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(any(ChatSession.class)))
                .thenAnswer(invocation -> {
                    ChatSession s = invocation.getArgument(0);
                    ChatSessionResponse r = new ChatSessionResponse();
                    r.setId(s.getId());
                    r.setName(s.getName());
                    r.setFavorite(s.isFavorite());
                    return r;
                });

        ChatSessionResponse result = chatSessionService.markAsFavorite(sessionId);

        assertTrue(result.getFavorite());
        verify(chatSessionRepository).save(chatSession);
    }

    @Test
    void unmarkAsFavoriteSuccessTest() {
        chatSession.setFavorite(true);
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(chatSessionMapper.toResponse(any(ChatSession.class)))
                .thenAnswer(invocation -> {
                    ChatSession s = invocation.getArgument(0);
                    ChatSessionResponse r = new ChatSessionResponse();
                    r.setId(s.getId());
                    r.setName(s.getName());
                    r.setFavorite(s.isFavorite());
                    return r;
                });

        ChatSessionResponse result = chatSessionService.unmarkAsFavorite(sessionId);

        assertFalse(result.getFavorite());
        verify(chatSessionRepository).save(chatSession);
    }
}
