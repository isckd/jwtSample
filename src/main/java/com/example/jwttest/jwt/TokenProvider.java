package com.example.jwttest.jwt;

import com.example.jwttest.dto.TokensDto;
import com.example.jwttest.exception.CustomException;
import com.example.jwttest.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider implements InitializingBean {

    private final String secret;
    private final long tokenValidityInSeconds;

    private final String refreshSecret;
    private final long refreshTokenValidityInSeconds;
    private Key key;
    private Key refreshKey;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.refresh-secret}") String refreshSecret,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds
            ) {
        this.secret = secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds * 1000;
        this.refreshSecret = refreshSecret;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecret);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);

    }

    public String createAccessToken(String userId) {
        long now = (new Date().getTime());
        Date validity = new Date(now + this.tokenValidityInSeconds);

        return Jwts.builder()
                .setSubject(userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public String createRefreshToken(String userId) {
        long now = (new Date().getTime());
        Date validity = new Date(now + this.tokenValidityInSeconds);

        return Jwts.builder()
                .setSubject(userId)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    public void validateUserInMAP(String userId, String ci) {

        // TODO 실제로 검증시켜야 함.
        if(userId.equals("test") && ci.equals("test")) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }
    }


    public TokensDto validateToken(TokensDto tokens) {
        try {
            String acToken = tokens.getAccessToken();   String rfToken = tokens.getRefreshToken();
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(acToken);        // accesstoken 검증
            return tokens;
        }
        catch (ExpiredJwtException e) {
            // TODO refreshToken 검증해야 됨?
        }
        catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_TOKEN);
        }
        catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_SIGNATURE);
        }
        return null;
    }
}
