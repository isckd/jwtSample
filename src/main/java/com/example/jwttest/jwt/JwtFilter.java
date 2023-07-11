package com.example.jwttest.jwt;

import com.example.jwttest.dto.TokenDto;
import com.example.jwttest.exception.CustomException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class JwtFilter extends GenericFilterBean {

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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest =  (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        log.debug("URI : " + httpServletRequest.getRequestURI());

        TokenDto tokenDto = resolveToken(httpServletRequest);                               // 토큰 정보를 꺼내온다.
        if (tokenDto == null || tokenDto.getToken() == null || tokenDto.getRefreshToken() == null) {
            filterChain.doFilter(servletRequest, servletResponse);                          // 토큰이 없으면 Security Context 에 저장하지 않는다.
            return;
        }

        String accessToken = tokenDto.getToken();
        String refreshToken = tokenDto.getRefreshToken();

        String jwt = "";
        try {
            jwt = tokenProvider.validateToken(accessToken, refreshToken);                 // validate 로 access, refresh token 검증
        } catch (CustomException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);             // 403, 권한 없음
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write(String.format("{\"error\": \"%s\"}", e.getMessage()));
            return;
        }

        if (StringUtils.hasText(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);           // 토큰 정보를 이용해 Authentication 객체를 생성한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);           // SecurityContext 에 Authentication 객체를 저장한다.
            log.debug("Security Context 에 '{}' 인증 정보를 저장", authentication.getName());
        } else {
            log.debug("유효한 JWT 토큰이 없습니다");
        }

        if (!jwt.equals(accessToken)) {                                                     // access token 새로 발급 시
            httpServletResponse.addHeader(AUTHORIZATION_HEADER, "Bearer " + jwt);     // response header 에 access token 추가
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
     */
    private TokenDto resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);                           // 헤더에서 토큰 정보를 꺼내온다.
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {            // 토큰 정보가 존재하고, Bearer로 시작하는 경우
            bearerToken = bearerToken.split(" ")[1].trim();                               // Bearer 다음 문자열을 반환한다.
            return new TokenDto(bearerToken, refreshToken);
        }
        return null;
    }
}
