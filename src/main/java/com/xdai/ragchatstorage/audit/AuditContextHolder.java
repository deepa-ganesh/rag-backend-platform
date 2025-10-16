package com.xdai.ragchatstorage.audit;

import com.xdai.ragchatstorage.dto.AuditContext;

public class AuditContextHolder {
    private static final ThreadLocal<AuditContext> CTX = new ThreadLocal<>();

    public static void setContext(AuditContext context) {
        CTX.set(context);
    }

    public static AuditContext getContext() {
        return CTX.get();
    }

    public static void clear() {
        CTX.remove();
    }
}
