package com.xdai.ragchatstorage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditContext {
    private String traceId;
    private String requester;
    private String httpMethod;
    private String requestUri;
    private String clientIp;
    private String userAgent;
}
