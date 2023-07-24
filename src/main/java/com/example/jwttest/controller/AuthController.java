package com.example.jwttest.controller;

import com.example.jwttest.dto.LoginDto;
import com.example.jwttest.dto.TokensDto;
import com.example.jwttest.jwt.JwtFilter;
import com.example.jwttest.jwt.TokenProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final TokenProvider tokenProvider;

    public AuthController(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World!");
    }


    /**
     * userId 와 ci 를 받아서 accessToken, refreshToken 을 발급하는 api
     */
    @PostMapping("/login")
    public ResponseEntity<TokensDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        // TODO   userid 와 ci 를 받아서 map 에 검증시키는 메서드 완성 필요. 이 때 유저의 정보를 받아와서 token 의 Payload 에 넣어버리자.
        tokenProvider.validateUserInMAP(loginDto.getUserId(), loginDto.getCi());

        String accessToken = tokenProvider.createAccessToken(loginDto.getUserId());
        String refreshToken = tokenProvider.createRefreshToken(loginDto.getUserId());
        TokensDto tokens = new TokensDto(accessToken, refreshToken);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/tokenTest")
    public ResponseEntity<String> validateAccessToken() {
        // TODO filter 에서 검증

        log.info("token is valid");
        return ResponseEntity.ok("Token is valid");
    }
}
