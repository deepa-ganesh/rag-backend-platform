package com.xdai.rag.chatstorage.audit;

import com.xdai.rag.chatstorage.dto.AuditContext;

public class AuditContextHolder {

    private static final ThreadLocal<AuditContext> AUDIT_CONTEXT = new ThreadLocal<>();

    public static void setContext(AuditContext context) {
        AUDIT_CONTEXT.set(context);
    }

    public static AuditContext getContext() {
        return AUDIT_CONTEXT.get();
    }

    public static void clear() {
        AUDIT_CONTEXT.remove();
    }
}
