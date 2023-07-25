package com.example.jwttest.jwt;

import com.example.jwttest.dto.TokensDto;
import com.example.jwttest.entity.RefreshToken;
import com.example.jwttest.exception.CustomException;
import com.example.jwttest.exception.ErrorCode;
import com.example.jwttest.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private final String secret;
    private final long tokenValidityInSeconds;
    private final RefreshTokenRepository refreshTokenRepository;
    private Key key;
    private Key refreshKey;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            RefreshTokenRepository refreshTokenRepository) {
        this.secret = secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds * 1000;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
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
//        refreshTokenRepository.findById(userId).ifPresent(refreshTokenRepository::delete);
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken refreshTokenObj = new RefreshToken(userId, refreshToken);
        refreshTokenRepository.save(refreshTokenObj);
        return refreshToken;
    }

    public void validateUserInMAP(String userId, String ci) {

        // TODO 실제로 검증시켜야 함.
        if(userId.equals("test") && ci.equals("test")) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }
    }


    public String validateToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();        // accessToken 검증
            return accessToken;
        }
        catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, e.getClaims(), "expired token");
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
    }
}
