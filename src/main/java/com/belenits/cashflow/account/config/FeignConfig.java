package com.belenits.cashflow.account.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.belenits.cashflow.account.util.CorrelationIdUtil;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor correlationIdInterceptor() {
        return requestTemplate -> {
            String correlationId = MDC.get(CorrelationIdUtil.MDC_KEY);
            if (correlationId != null) {
                requestTemplate.header(CorrelationIdUtil.HEADER_NAME, correlationId);
            }
        };
    }
}