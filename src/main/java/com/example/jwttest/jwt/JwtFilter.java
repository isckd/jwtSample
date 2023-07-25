package com.example.jwttest.jwt;

import com.example.jwttest.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    public static final String address = "http://localhost:8080";
    private final TokenProvider tokenProvider;
    private final RestTemplate restTemplate;

    public JwtFilter(TokenProvider tokenProvider, RestTemplate restTemplate) {
        this.tokenProvider = tokenProvider;
        this.restTemplate = restTemplate;
    }

    // TODO refresh token 은 나중에 따로 받는걸로?
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String URI = request.getRequestURI();
        log.info("URI : " + URI);

        String accessToken = resolveAccessToken(request);                   // 토큰 정보 꺼내오고,
        if(accessToken == null) {                                           // 토큰 없으면 필터 동작 끝내기
            filterChain.doFilter(request, response);
            return;
        }

        try {
             String newAccessToken = tokenProvider.validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            log.error("Access Token expired");
            // TODO 실제로는 앱/웹 단에 요청을 해야 하는데, 해당 api 를 받은 앱/웹은 서버에 refreshToken 을 전달해야 한다. 여기서는 Mock Controller 로 보낸다.
            String refreshToken = restTemplate.getForObject(address + "/api/mock/getRefresh" + "?originURI=" + URI + "&userId=" + e.getClaims().getSubject(), String.class);

            // TODO 여기서 로직을 어떻게 처리할건지?

        } catch (CustomException e) {
            log.error("Token is not valid : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }

    }


    /**
     * 헤더에서 accessToken, refreshToken 을 가져온다.
     */
    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
