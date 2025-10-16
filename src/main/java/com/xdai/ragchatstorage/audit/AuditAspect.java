package com.xdai.ragchatstorage.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdai.ragchatstorage.dto.AuditContext;
import com.xdai.ragchatstorage.entity.AuditEvent;
import com.xdai.ragchatstorage.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditEventRepository auditRepo;
    private final ObjectMapper objectMapper;

    @Around("@annotation(audited)")
    public Object around(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        AuditContext auditContext = AuditContextHolder.getContext();
        AuditStatus status = AuditStatus.SUCCESS;
        Object result;
        String responseBody = null;
        String errorCode = null;
        String errorMessage = null;
        int httpStatus = HttpStatus.OK.value();

        try {
            result = pjp.proceed();

            if (audited.includeResponse() && result != null) {
                responseBody = safeJson(result);
            }

            return result;
        } catch (Exception ex) {
            status = AuditStatus.FAILURE;
            errorMessage = ex.getMessage();
            errorCode = ex.getClass().getSimpleName();
            httpStatus = mapStatus(ex);

            throw ex;
        } finally {
            try {
                AuditEvent event = AuditEvent.builder()
                        .traceId(nullSafe(auditContext != null ? auditContext.getTraceId() : null))
                        .requester(nullSafe(auditContext != null ? auditContext.getRequester() : null))
                        .action(audited.action())
                        .status(status)
                        .httpMethod(nullSafe(auditContext != null ? auditContext.getHttpMethod() : null))
                        .requestUri(nullSafe(auditContext != null ? auditContext.getRequestUri() : null))
                        .httpStatus(httpStatus)
                        .responseBody(trim(responseBody))
                        .errorCode(errorCode)
                        .errorMessage(trim(errorMessage))
                        .clientIp(nullSafe(auditContext != null ? auditContext.getClientIp() : null))
                        .userAgent(nullSafe(auditContext != null ? auditContext.getUserAgent() : null))
                        .build();

                auditRepo.save(event);

                log.info("AUDIT_EVENT: {}", safeJson(event)); // structured log for ELK
            } catch (Exception ex) {
                log.error("Audit persist failed: {}", ex.toString());
            }
        }
    }

    private int mapStatus(Exception ex) {
        String name = ex.getClass().getSimpleName();

        return switch (name) {
            case "InvalidRequestException" -> HttpStatus.BAD_REQUEST.value();
            case "ChatSessionNotFoundException", "ChatMessageNotFoundException" -> HttpStatus.NOT_FOUND.value();
            case "ApiKeyAuthenticationException" -> HttpStatus.UNAUTHORIZED.value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };
    }

    private String safeJson(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);

            return trim(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private String trim(String message) {
        return (message != null && message.length() > 4000) ? message.substring(0, 4000) + "..." : message;
    }
}
