package com.xdai.rag.chatstorage.filter;

import com.xdai.rag.chatstorage.ratelimiter.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Order(2)
@Slf4j
public class RateLimitFilter extends BaseFilter {

    private final RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternalWithSecurity(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {

        String clientId = request.getRemoteAddr();

        if (!rateLimiterService.isAllowed(clientId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
