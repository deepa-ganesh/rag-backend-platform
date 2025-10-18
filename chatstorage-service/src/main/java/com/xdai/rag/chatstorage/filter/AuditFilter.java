package com.xdai.rag.chatstorage.filter;

import com.xdai.rag.chatstorage.audit.AuditContextHolder;
import com.xdai.rag.chatstorage.dto.AuditContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(3)
@Slf4j
public class AuditFilter extends BaseFilter {

    @Override
    protected void doFilterInternalWithSecurity(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {

        String traceId = request.getHeader("X-Correlation-Id");
        if (StringUtils.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        String requester = (String) request.getAttribute("requester");
        if (StringUtils.isBlank(requester)) {
            requester = "anonymous";
        }

        AuditContext auditContext = AuditContext.builder()
                .traceId(traceId)
                .requester(requester)
                .httpMethod(request.getMethod())
                .requestUri(request.getRequestURI())
                .clientIp(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build();

        AuditContextHolder.setContext(auditContext);
        response.setHeader("X-Correlation-Id", traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            AuditContextHolder.clear();
        }
    }
}
