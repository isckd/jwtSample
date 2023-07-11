package com.example.jwttest.jwt;

import com.example.jwttest.entity.RefreshToken;
import com.example.jwttest.exception.CustomException;
import com.example.jwttest.exception.ErrorCode;
import com.example.jwttest.repository.RefreshTokenRepository;
import com.example.jwttest.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@Log4j2
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";       // 토큰에 담길 권한 정보의 키

    private final String secret;
    private final long tokenValidityInMilliseconds;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds, RefreshTokenRepository refreshTokenRepository, UserDetailsService userDetailsService) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }


    /**
     * ${jwt.secret} 값을 Base64 Decode 하여 key 변수에 할당한다.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);   // Base64 Decode
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication 객체의 권한정보를 이용해서 토큰을 생성하는 메소드
     */
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())       // 사용자 이름
                .claim(AUTHORITIES_KEY, authorities)        // authorities : "ROLE_USER" or "ROLE_ADMIN"
                .signWith(key, SignatureAlgorithm.HS512)    // HMAC SHA512 알고리즘을 사용해서 sign
                .setExpiration(validity)                    // 토큰의 만료시간 설정
                .compact();                                 // 직렬화
    }


    /**
     * 토큰을 파라미터로 받아서 토큰에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메소드
     */
    public Authentication getAuthentication(String token) {
        // 토큰을 이용해서 Claims 객체를 생성한다.
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Claims 객체에서 권한 정보를 가져온다.
        Collection<? extends GrantedAuthority> authorities  =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 권한 정보를 이용해서 User 객체를 생성하고 리턴한다. 여기서 User 객체는 UserDetails 인터페이스를 구현한 객체이다. (User Entity 가 아님)
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰의 유효성 검증을 수행하는 메소드
     */
    public String validateToken(String token, String refreshToken) throws CustomException{
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return token;
        } catch (ExpiredJwtException e) {
            log.info(e.getMessage() + " 만료된 access 토큰");

            String expiredTokenUsername = e.getClaims().getSubject();
            RefreshToken storedRefreshToken = refreshTokenRepository.findById(expiredTokenUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_REFRESH_TOKEN));
            if((storedRefreshToken.getRefreshToken()).equals(refreshToken)) {
                log.info("access token 만료되었지만, refresh token 일치하여 재발급");
                return createNewToken(expiredTokenUsername);
            } else {
                throw new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);
            }
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_SIGNATURE);
        }
    }

    /**
     * access token 만료, refresh token 일치시 access token 재발급하여 SecurityContext 에 저장
     */
    private String createNewToken(String expiredTokenUsername) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(expiredTokenUsername);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return createToken(authentication);
    }

}
