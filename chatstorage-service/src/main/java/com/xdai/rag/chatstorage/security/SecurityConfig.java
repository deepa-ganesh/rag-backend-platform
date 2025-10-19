package com.xdai.rag.chatstorage.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Option A: Keep SecurityConfig minimal. Disable CSRF for stateless APIs
        // and let custom servlet filters (Authentication/RateLimit/Audit) handle access control.
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
