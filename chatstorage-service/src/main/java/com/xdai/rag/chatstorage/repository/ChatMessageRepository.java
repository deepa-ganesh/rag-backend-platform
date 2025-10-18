package com.xdai.rag.chatstorage.repository;

import com.xdai.rag.chatstorage.entity.ChatMessage;
import com.xdai.rag.chatstorage.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.session.id = :sessionId")
    int deleteBySessionId(UUID sessionId);

    Page<ChatMessage> findBySessionOrderByCreatedAtAsc(ChatSession session, PageRequest pageable);
}
