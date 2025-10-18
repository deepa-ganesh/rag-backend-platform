package com.xdai.rag.chatstorage.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class AuthenticationFilter extends BaseFilter {

    @Value("${app.api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternalWithSecurity(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {

        String headerKey = request.getHeader("X-API-Key");

        if (headerKey == null || !headerKey.equals(apiKey)) {
            log.warn("Unauthorized access attempt: {}", request.getRequestURI());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or missing API key");

            return;
        }

        filterChain.doFilter(request, response);
    }
}
