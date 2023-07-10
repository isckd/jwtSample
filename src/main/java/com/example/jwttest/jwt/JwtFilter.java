package com.example.jwttest.jwt;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Log4j2
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";      // 헤더의 키 값

    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     *  토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest =  (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);        // 토큰 정보를 꺼내온다.
        String requestURI = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);     // 토큰 정보를 이용해 Authentication 객체를 생성한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);     // SecurityContext에 Authentication 객체를 저장한다.
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    /**
     * Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);                    // 헤더에서 토큰 정보를 꺼내온다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {     // 토큰 정보가 존재하고, Bearer로 시작하는 경우
            return bearerToken.substring(7);                                    // Bearer 다음 문자열을 반환한다.
        }
        return null;

    }
}
