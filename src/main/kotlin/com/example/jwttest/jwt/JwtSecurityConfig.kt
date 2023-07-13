package com.example.jwttest.jwt

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * JwtFilter 를 SecurityConfig 에 적용할 때 사용
 */
class JwtSecurityConfig(private val tokenProvider: TokenProvider) :
    SecurityConfigurerAdapter<DefaultSecurityFilterChain?, HttpSecurity>() {
    /**
     * JwtFilter 를 Security 로직에 필터로 등록
     */
    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(
            JwtFilter(tokenProvider),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }
}