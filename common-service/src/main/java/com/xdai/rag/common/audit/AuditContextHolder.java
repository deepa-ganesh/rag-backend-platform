package com.xdai.rag.common.audit;

import com.xdai.rag.common.dto.AuditContext;

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
