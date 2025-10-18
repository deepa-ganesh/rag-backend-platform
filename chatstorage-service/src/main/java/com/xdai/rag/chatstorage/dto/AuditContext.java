package com.xdai.rag.chatstorage.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuditContext {

    private String traceId;
    private String requester;
    private String httpMethod;
    private String requestUri;
    private String clientIp;
    private String userAgent;
}
