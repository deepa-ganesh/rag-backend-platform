package com.xdai.ragchatstorage.repository;

import com.xdai.ragchatstorage.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    Page<ChatSession> findByFavorite(Boolean favorite, PageRequest pageable);
}
