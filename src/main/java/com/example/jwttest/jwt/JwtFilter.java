package com.example.jwttest.jwt;

import com.example.jwttest.dto.TokenDto;
import com.example.jwttest.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;



/**
 * 토큰의 인증정보를 SecurityContext 에 보내는 역할
 * OncePerRequestFilter : 모든 요청에 대해 단 한번만 필터링을 수행한다. (기본 서블릿 필터는 DispatcherServlet 을 들어가고 나올 때마다 수행된다.)
 */
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";      // 헤더의 키 값
    public static final String REFRESH_TOKEN_HEADER = "refreshToken";      // refresh token 키 값

    private TokenProvider tokenProvider;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public JwtFilter(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     *  토큰의 인증정보를 SecurityContext 에 보내는 역할
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.debug("URI : " + httpServletRequest.getRequestURI());

        Optional<TokenDto> tokenDto = resolveToken(httpServletRequest);                                       // 토큰 정보를 꺼내온다.
        if (!tokenDto.isPresent()) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);                                    // 토큰이 없으면 토큰 검증을 실시하지 않는다.
            return;
        }

        try {
            TokenDto newTokens = tokenProvider.validateToken(tokenDto.get().getToken(), tokenDto.get().getRefreshToken());
            processValidTokens(tokenDto.get().getToken() , newTokens, httpServletRequest, httpServletResponse, filterChain);
        } catch (CustomException e) {
            jwtAuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, e);
        }
    }

    /**
     * Request Header 에서 토큰 정보를 꺼내오기 위한 메서드
     */
    private Optional<TokenDto> resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION_HEADER);                           // 헤더에서 토큰 정보를 꺼내온다.
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);

        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {            // 토큰 정보가 존재하고, Bearer로 시작하는 경우
            accessToken = accessToken.split(" ")[1].trim();                               // Bearer 다음 문자열을 반환한다.
            return Optional.of(new TokenDto(accessToken, refreshToken));
        }
        return Optional.empty();
    }

    /**
     * 토큰이 유효할 때 SecurityContext 에 저장하는 메서드
     */
    private void processValidTokens(String accessToken, TokenDto newTokens, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (StringUtils.hasText(newTokens.getToken())) {
            Authentication authentication = tokenProvider.getAuthentication(newTokens.getToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug(authentication.getName() + " 의 접근 허용. Security Context 에 저장 완료");

            if (!newTokens.getToken().equals(accessToken)) {
                response.addHeader(AUTHORIZATION_HEADER, "Bearer " + newTokens.getToken());
                response.addHeader(REFRESH_TOKEN_HEADER, newTokens.getRefreshToken());
            }
        } else {
            log.debug("No valid JWT token");
        }

        chain.doFilter(request, response);
    }
}
