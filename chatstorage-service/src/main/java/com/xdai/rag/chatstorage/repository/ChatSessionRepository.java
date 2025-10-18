package com.xdai.rag.chatstorage.repository;

import com.xdai.rag.chatstorage.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
}
