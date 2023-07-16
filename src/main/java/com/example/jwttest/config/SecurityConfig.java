package com.example.jwttest.config;

import com.example.jwttest.jwt.JwtAccessDeniedHandler;
import com.example.jwttest.jwt.JwtAuthenticationEntryPoint;
import com.example.jwttest.jwt.JwtFilter;
import com.example.jwttest.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)      // @PreAuthroize 어노테이션을 메서드 단위로 추가하기 위해 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final CorsConfig corsConfig;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsConfig corsConfig,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsConfig = corsConfig;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**",
                        "/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()                                       // 토큰을 쿠키로 보내지 않으므로 disable

                .addFilterBefore(corsConfig.corsFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(tokenProvider, jwtAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()                                    // Exception 설정 커스텀한것들로 변경
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 인증 실패시 처리
                .accessDeniedHandler(jwtAccessDeniedHandler)            // 인가실패시 처리

                .and()                                                  // h2-console 을 위한 설정 (운영 시에는 빼야됨!)
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()                                                  // 세션을 사용하지 않기 때문에 STATELESS 로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll()
                .antMatchers("/api/authenticate").permitAll()          // 로그인, 회원가입 API 는 누구나 접근 가능
                .antMatchers("/api/signup").permitAll()
                .antMatchers("/api/user").hasRole("USER")
                .antMatchers("/api/user/{username}").hasRole("ADMIN")

                .antMatchers("/swagger-ui/**").permitAll()             // swagger-ui 허용 (운영 시에는 빼야됨!)
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v3/**").permitAll()

                .anyRequest().authenticated();

    }
}
