package com.example.jwttest.config;

import com.example.jwttest.jwt.JwtFilter;
import com.example.jwttest.jwt.TokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;

@Configuration
public class FilterConfig {

    private final TokenProvider tokenProvider;
    private final RestTemplate restTemplate;

    public FilterConfig(TokenProvider tokenProvider, RestTemplate restTemplate) {
        this.tokenProvider = tokenProvider;
        this.restTemplate = restTemplate;
    }
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(tokenProvider, restTemplate);
    }

    @Bean
    public FilterRegistrationBean<Filter> jwtFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter());
        registration.addUrlPatterns("/api/v2/*");  // 필터가 적용될 URL 패턴
        registration.setName("jwtFilter");  // 필터 이름
        registration.setOrder(1);  // 필터 순서
        return registration;
    }
}
