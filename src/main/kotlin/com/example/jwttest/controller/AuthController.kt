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
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto?.username, loginDto?.password)
        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = tokenProvider.createToken(authentication)
        val refreshToken = customUserDetailsService.generateRefreshToken(loginDto?.username)
        val httpHeaders = HttpHeaders()
        httpHeaders.add(JwtFilter.Companion.AUTHORIZATION_HEADER, "Bearer $jwt") // 헤더에 토큰을 넣는다.
        httpHeaders.add(JwtFilter.Companion.REFRESH_TOKEN_HEADER, refreshToken)
        return ResponseEntity(TokenDto(jwt, refreshToken), httpHeaders, HttpStatus.OK) // 임시로 바디에도 넣는다.
    }
}