package com.example.jwttest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);           //  내 서버가 응답할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
        config.addAllowedOriginPattern("*");        //  Origin(출처 : protocol + host + path.  ex : http://localhost:8080 ) 을 모두 허용하겠다.
        config.addAllowedHeader("*");               //  모든 header 에 응답을 허용하겠다.
        config.addAllowedMethod("*");               //  모든 post, get, put, delete, patch 요청을 허용하겠다.

        source.registerCorsConfiguration("/api/**", config);    //  /api/** 에 대한 Cors 를 허용하겠다.
        return new CorsFilter(source);
    }

}