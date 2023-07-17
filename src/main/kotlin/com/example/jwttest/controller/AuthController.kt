package com.example.jwttest.controller

import com.example.jwttest.dto.LoginDto
import com.example.jwttest.dto.TokenDto
import com.example.jwttest.jwt.JwtFilter
import com.example.jwttest.jwt.TokenProvider
import com.example.jwttest.service.CustomUserDetailsService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class AuthController(
    private val tokenProvider: TokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val customUserDetailsService: CustomUserDetailsService
) {
    /**
     * 로그인으로 토큰을 발급받는다.
     */
    @PostMapping("/authenticate")
    fun authorize(@RequestBody loginDto: @Valid LoginDto?): ResponseEntity<TokenDto> {

        /* username, password 를 받아 Authentication 객체를 생성한다.
         * .authenticate() 를 통해 UserDetailsService 의 loadUserByUsername() 메소드를 거쳐 유저 인증을 한다.
         * 인증이 성공하면 SecurityContextHolder 에 User 정보를 저장한다.
         */
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto?.username, loginDto?.password)
        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)         // 유저 정보를 사용해서 Authentication 객체를 만든다.
        SecurityContextHolder.getContext().authentication = authentication

        val jwt = tokenProvider.createToken(authentication)
        val refreshToken = customUserDetailsService.generateRefreshToken(loginDto?.username)
        val httpHeaders = HttpHeaders()
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer $jwt")                   // 헤더에 토큰을 넣는다.
        httpHeaders.add(JwtFilter.REFRESH_TOKEN_HEADER, refreshToken)
        return ResponseEntity(TokenDto(jwt, refreshToken), httpHeaders, HttpStatus.OK)              // 임시로 바디에도 넣는다.
    }

    /**
     * SecurityContext = 인증 정보를 저장하는 Spring Security 에서 제공하는 인터페이스
     * SecurityContextHolder = SecurityContext 를 저장하기 위한 헬퍼 클래스.
     * Default 로 THREAD 당 하나의 인스턴스가 생성되며, 이를 통해 인증된 유저 정보를 꺼내 쓸 수 있다. 즉, THREADLOCAL 하다는 것이다.
     * 이로 인해 다른 쓰레드에서는 인증된 정보를 참조할 수 없다.   -> 이것이 핵심이다.
     *
     * THREADLOCAL 말고도 다른 전략이 있으나, 일반적으로 사용하지 않는다.
     */
}