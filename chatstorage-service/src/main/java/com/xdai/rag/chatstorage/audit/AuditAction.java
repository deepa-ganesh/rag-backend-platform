package com.xdai.rag.chatstorage.audit;

public enum AuditAction {
    // Session actions
    SESSION_CREATE,
    SESSION_RENAME,
    SESSION_DELETE,
    SESSION_MARK_FAVORITE,
    SESSION_UNMARK_FAVORITE,
    SESSIONS_LIST,

    // Message actions
    MESSAGE_CREATE,
    MESSAGES_LIST_BY_SESSION,
    MESSAGES_DELETE_BY_SESSION,
    MESSAGE_DELETE
}
