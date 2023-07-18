package com.example.jwttest.config

import com.example.jwttest.jwt.JwtAccessDeniedHandler
import com.example.jwttest.jwt.JwtAuthenticationEntryPoint
import com.example.jwttest.jwt.JwtFilter
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
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthroize 어노테이션을 메서드 단위로 추가하기 위해 적용

class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val corsConfig: CorsConfig,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(web: WebSecurity) {                                      // h2-console 을 사용하기 위함. 운영 시 삭제
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
            .csrf().disable()                                                       // 토큰을 쿠키로 보내지 않으므로 disable

            .addFilterBefore(corsConfig.corsFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(JwtFilter(tokenProvider, jwtAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter::class.java)

            .exceptionHandling()                                                    // Exception 설정 커스텀한것들로 변경
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

            .and()                                                                  // h2-console 을 위한 설정 (운영 시에는 빼야됨!)
                .headers()
                .frameOptions()
                .sameOrigin()

            .and()                                                                  // 세션을 사용하지 않기 때문에 STATELESS 로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/user").hasRole("USER")
                .antMatchers("/api/user/{username}").hasRole("ADMIN")
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()

                .antMatchers("/swagger-ui/**").permitAll()                        // swagger-ui 허용 (운영 시에는 빼야됨!)
                .antMatchers("/swagger-resources/**").permitAll()
//                .antMatchers("/v3/**").permitAll()
                .anyRequest().authenticated()
    }
}