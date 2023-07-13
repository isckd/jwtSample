package com.example.jwttest.config

import com.example.jwttest.handler.JwtAccessDeniedHandler
import com.example.jwttest.jwt.JwtAuthenticationEntryPoint
import com.example.jwttest.jwt.JwtSecurityConfig
import com.example.jwttest.jwt.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.CorsFilter

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthroize 어노테이션을 메서드 단위로 추가하기 위해 적용

class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val corsFilter: CorsFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(web: WebSecurity) {
        web
            .ignoring()
            .antMatchers(
                "/h2-console/**",
                "/favicon.ico"
            )
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable() // token 방식이라 csrf 설정 disable
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling() // Exception 설정 커스텀한것들로 변경
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .and() // h2-console 을 위한 설정
            .headers()
            .frameOptions()
            .sameOrigin()
            .and() // 세션을 사용하지 않기 때문에 STATELESS 로 설정
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/hello").permitAll()
            .antMatchers("/api/authenticate").permitAll() // 로그인, 회원가입 API 는 누구나 접근 가능
            .antMatchers("/api/signup").permitAll()
            .antMatchers("/swagger-ui/**").permitAll() // swagger-ui 허용
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/v3/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .apply(JwtSecurityConfig(tokenProvider)) // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
    }
}