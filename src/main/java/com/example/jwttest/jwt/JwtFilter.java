package com.example.jwttest.jwt;

import com.example.jwttest.dto.TokenDto;
import com.example.jwttest.exception.CustomException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Log4j2
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";      // 헤더의 키 값
    public static final String REFRESH_TOKEN_HEADER = "refreshToken";      // refresh token 키 값

    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
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
            processValidTokens(newTokens, httpServletRequest, httpServletResponse, filterChain);
        } catch (CustomException e) {
            handleInvalidTokens(e, httpServletResponse);
        }

//        String newAccessToken = "";
//        String newRefreshToken = "";
//        try {
//            TokenDto newtokens = tokenProvider.validateToken(accessToken, refreshToken);         // validate 로 access, refresh token 만료 검증
//            newAccessToken = newtokens.getToken();
//            newRefreshToken = newtokens.getRefreshToken();
//        } catch (CustomException e) {
//            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);                            // 403, 권한 없음
//            httpServletResponse.setContentType("application/json");
//            httpServletResponse.getWriter().write(String.format("{\"error\": \"%s\"}", e.getMessage()));
//            log.error("잘못된 접근 발생! 로그 확인 요망");
//            return;
//        }

//        if (StringUtils.hasText(newAccessToken)) {
//            Authentication authentication = tokenProvider.getAuthentication(newAccessToken);           // 토큰 정보를 이용해 Authentication 객체를 생성한다.
//            SecurityContextHolder.getContext().setAuthentication(authentication);           // SecurityContext 에 Authentication 객체를 저장한다.
//            log.debug("Security Context 에 '{}' 인증 정보를 저장", authentication.getName());
//        } else {
//            log.debug("유효한 JWT 토큰이 없습니다");
//        }
//
//        if (!newAccessToken.equals(accessToken)) {                                                     // access token 새로 발급 시
//            httpServletResponse.addHeader(AUTHORIZATION_HEADER, "Bearer " + newAccessToken);     // response header 에 access token 추가
//            httpServletResponse.addHeader(REFRESH_TOKEN_HEADER, newRefreshToken);           // access token 새로 발급 시 refresh token 도 새로 발급되므로 넣는다.
//        }
//
//        filterChain.doFilter(httpServletRequest, httpServletResponse);
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
     *  토큰이 유효하지 않을 때 처리하는 메서드
     */
    private void handleInvalidTokens(CustomException e, HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", e.getMessage()));
        log.error("잘못된 접근 발생! 로그 확인 요망");
    }

    /**
     * 토큰이 유효할 때 SecurityContext 에 저장하는 메서드
     */
    private void processValidTokens(TokenDto newTokens, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (StringUtils.hasText(newTokens.getToken())) {
            Authentication authentication = tokenProvider.getAuthentication(newTokens.getToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Save '{}' authentication information in Security Context", authentication.getName());

            if (!newTokens.getToken().equals(newTokens.getToken())) {
                response.addHeader(AUTHORIZATION_HEADER, "Bearer " + newTokens.getToken());
                response.addHeader(REFRESH_TOKEN_HEADER, newTokens.getRefreshToken());
            }
        } else {
            log.debug("No valid JWT token");
        }

        chain.doFilter(request, response);
    }
}
