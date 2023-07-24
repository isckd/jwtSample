package com.example.jwttest.jwt;

import com.example.jwttest.dto.TokensDto;
import com.example.jwttest.exception.CustomException;
import jdk.nashorn.internal.parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // TODO refresh token 은 나중에 따로 받는걸로?
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("URI : " + request.getRequestURI());

        Optional<TokensDto> tokens = resolveToken(request);         // 토큰 정보 꺼내오고,
        if(!tokens.isPresent()) {                                   // 토큰 없으면 필터 동작 끝내기
            filterChain.doFilter(request, response);
            return;
        }

        try {
            TokensDto newTokens = tokenProvider.validateToken(tokens.get());
        } catch (CustomException e) {
            log.error("Token is not valid : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }

    }


    /**
     * 헤더에서 accessToken, refreshToken 을 가져온다.
     */
    private Optional<TokensDto> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            String refreshToken = request.getHeader("RefreshToken");
            return Optional.of(new TokensDto(accessToken, refreshToken));
        }
        return Optional.empty();
    }
}
