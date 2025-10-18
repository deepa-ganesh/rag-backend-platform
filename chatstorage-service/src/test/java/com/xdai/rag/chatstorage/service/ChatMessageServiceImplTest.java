package com.xdai.rag.chatstorage.service;

import com.xdai.rag.chatstorage.common.Sender;
import com.xdai.rag.chatstorage.dto.ChatMessageRequest;
import com.xdai.rag.chatstorage.dto.ChatMessageResponse;
import com.xdai.rag.chatstorage.entity.ChatMessage;
import com.xdai.rag.chatstorage.entity.ChatSession;
import com.xdai.rag.chatstorage.mapper.ChatMessageMapper;
import com.xdai.rag.chatstorage.repository.ChatMessageRepository;
import com.xdai.rag.chatstorage.repository.ChatSessionRepository;
import com.xdai.rag.common.exception.ChatSessionNotFoundException;
import com.xdai.rag.chatstorage.service.impl.ChatMessageServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    private static final String CONTENT = "Hello!";;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private UUID sessionId;
    private ChatSession chatSession;
    private ChatMessageRequest messageRequest;
    private ChatMessage messageEntity;
    private ChatMessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();

        chatSession = new ChatSession();
        chatSession.setId(sessionId);

        messageRequest = new ChatMessageRequest();
        messageRequest.setSender(Sender.USER);
        messageRequest.setContent(CONTENT);

        messageEntity = new ChatMessage();
        messageEntity.setId(UUID.randomUUID());
        messageEntity.setSession(chatSession);
        messageEntity.setContent(CONTENT);

        messageResponse = new ChatMessageResponse();
        messageResponse.setId(messageEntity.getId());
        messageResponse.setContent(CONTENT);
    }

    @Test
    void addMessageSuccessTest() {
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageMapper.toEntity(messageRequest)).thenReturn(messageEntity);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(messageEntity);
        when(chatMessageMapper.toResponse(messageEntity)).thenReturn(messageResponse);

        ChatMessageResponse result = chatMessageService.addMessage(sessionId, messageRequest);

        assertEquals(messageResponse, result);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void addMessageSessionNotFoundTest() {
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(ChatSessionNotFoundException.class, () -> chatMessageService.addMessage(sessionId, messageRequest));
    }

    @Test
    void getMessagesBySessionIdSuccessTest() {
        Page<ChatMessage> page = new PageImpl<>(List.of(messageEntity));
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(chatSession));
        when(chatMessageRepository.findBySessionOrderByCreatedAtAsc(eq(chatSession), any())).thenReturn(page);
        when(chatMessageMapper.toResponseList(anyList())).thenReturn(List.of(messageResponse));

        List<ChatMessageResponse> responses = chatMessageService.getMessagesBySessionId(sessionId, 0, 10);

        assertEquals(1, responses.size());
        verify(chatMessageRepository).findBySessionOrderByCreatedAtAsc(eq(chatSession), any());
    }

    @Test
    void deleteMessageByIdSuccessTest() {
        UUID msgId = UUID.randomUUID();
        when(chatMessageRepository.existsById(msgId)).thenReturn(true);

        chatMessageService.deleteMessageById(msgId);

        verify(chatMessageRepository).deleteById(msgId);
    }

    @Test
    void deleteMessageByIdNotFoundTest() {
        UUID msgId = UUID.randomUUID();
        when(chatMessageRepository.existsById(msgId)).thenReturn(false);

        assertThrows(ChatSessionNotFoundException.class, () -> chatMessageService.deleteMessageById(msgId));
    }

    @Test
    void deleteMessagesBySessionIdSuccessTest() {
        when(chatSessionRepository.existsById(sessionId)).thenReturn(true);
        when(chatMessageRepository.deleteBySessionId(sessionId)).thenReturn(3);

        int count = chatMessageService.deleteMessagesBySessionId(sessionId);

        assertEquals(3, count);
        verify(chatMessageRepository).deleteBySessionId(sessionId);
    }
}

