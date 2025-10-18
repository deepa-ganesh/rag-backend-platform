package com.xdai.rag.chatstorage.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public abstract class BaseFilter extends OncePerRequestFilter {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/actuator",
            "/actuator/health",
            "/actuator/info",
            "/health",
            "/swagger",
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/error"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (shouldSkipFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        doFilterInternalWithSecurity(request, response, filterChain);
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(p -> uri.startsWith(contextPath + p));
    }

    protected abstract void doFilterInternalWithSecurity(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         FilterChain filterChain) throws ServletException, IOException;
}
