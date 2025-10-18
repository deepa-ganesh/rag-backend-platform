package com.xdai.rag.chatstorage.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdai.rag.chatstorage.entity.AuditEvent;
import com.xdai.rag.chatstorage.repository.AuditEventRepository;
import com.xdai.rag.common.audit.AuditContextHolder;
import com.xdai.rag.common.audit.AuditStatus;
import com.xdai.rag.common.audit.Audited;
import com.xdai.rag.common.dto.AuditContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
@Slf4j
public class AuditAspect {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(audited)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Audited auditedAnnotation) throws Throwable {
        
        AuditContext auditContext = AuditContextHolder.getContext();
        
        AuditEvent auditEvent = constructAuditEvent(auditContext);
        auditEvent.setAction(auditedAnnotation.action());
        
        try {
            Object result = proceedingJoinPoint.proceed();

            if (auditedAnnotation.includeResponse() && result != null) {
                String responseBody = convertToJsonString(result);
                auditEvent.setResponseBody(responseBody);
            }
            auditEvent.setStatus(AuditStatus.SUCCESS);
            auditEvent.setHttpStatus(HttpStatus.OK.value());
            
            return result;

        } catch (Exception e) {
            auditEvent.setStatus(AuditStatus.FAILURE);
            auditEvent.setHttpStatus(getHttpStatus(e));
            auditEvent.setErrorMessage(e.getMessage());
            auditEvent.setErrorCode(e.getClass().getSimpleName());

            throw e;

        } finally {
            try {
                AuditEvent savedAuditEvent = auditEventRepository.save(auditEvent);

                log.info("AUDIT_EVENT: {}", convertToJsonString(savedAuditEvent));
            
            } catch (Exception e) {
                log.error("Unexpected error while auditing the action [{}]: {}", auditedAnnotation.action(), e.getMessage(), e);
            }
        }
    }

    private int getHttpStatus(Exception e) {
        String name = e.getClass().getSimpleName();

        return switch (name) {
            case "InvalidRequestException" -> HttpStatus.BAD_REQUEST.value();
            case "ChatSessionNotFoundException", "ChatMessageNotFoundException" -> HttpStatus.NOT_FOUND.value();
            case "ApiKeyAuthenticationException" -> HttpStatus.UNAUTHORIZED.value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };
    }

    private String convertToJsonString(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return trimJson(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String trimJson(String message) {
        return message != null && message.length() > 4000 ? 
                message.substring(0, 4000) + "..." : 
                message;
    }
    
    private AuditEvent constructAuditEvent(AuditContext auditContext) {
        if (auditContext == null) {
            return AuditEvent.builder().build();
        } else  {
            return AuditEvent.builder()
                    .traceId(auditContext.getTraceId())
                    .requester(auditContext.getRequester())
                    .httpMethod(auditContext.getHttpMethod())
                    .requestUri(auditContext.getRequestUri())
                    .clientIp(auditContext.getClientIp())
                    .userAgent((auditContext.getUserAgent()))
                    .build();
        }
    }
}
