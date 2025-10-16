package com.xdai.ragchatstorage.entity;

import com.xdai.ragchatstorage.dto.ChatSessionRequest;
import com.xdai.ragchatstorage.dto.ChatSessionResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_sessions")
@Entity
public abstract class ChatSession {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean favorite = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();

    public abstract Page<ChatSessionResponse> getAllSessions(int page, int size, Boolean favorite);

    public abstract ChatSessionResponse createSession(ChatSessionRequest chatSessionRequest);

    public abstract void deleteSession(UUID sessionId);

    public abstract ChatSessionResponse renameSession(UUID sessionId, String newSessionName);

    public abstract ChatSessionResponse markAsFavorite(UUID sessionId);
}
