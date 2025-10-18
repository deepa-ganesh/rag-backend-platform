package com.xdai.rag.chatstorage.repository;

import com.xdai.rag.chatstorage.audit.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}
