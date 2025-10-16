package com.xdai.ragchatstorage.config;

import com.xdai.ragchatstorage.audit.AuditContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatStorageConfig {

    @Bean
    public FilterRegistrationBean<AuditContextFilter> auditContextFilter() {
        FilterRegistrationBean<AuditContextFilter> registrationBean = new FilterRegistrationBean<>(new AuditContextFilter());
        registrationBean.setOrder(20); // run after authentication filter
        return registrationBean;
    }
}
