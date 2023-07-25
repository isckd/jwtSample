package com.example.jwttest.controller;

import com.example.jwttest.dto.LoginDto;
import com.example.jwttest.dto.TokensDto;
import com.example.jwttest.jwt.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final TokenProvider tokenProvider;
    private final RestTemplate restTemplate;

    public AuthController(TokenProvider tokenProvider, RestTemplate restTemplate) {
        this.tokenProvider = tokenProvider;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/hello2")
    public String hello2() {return "redirect:/api/v1/hello";}


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

    /**
     * 웹/앱에서 보낸 accessToken 이 만료되었을 때, 서버는 웹/앱에 refreshToken 을 요청하는 api 로 redirect 하고,
     * 웹/앱은 refreshToken 을 담아 보내는 API 가 아래의 것.
     */
    @PostMapping("/refresh")
    public String refreshTokenAuthorize(@RequestParam String originURI, @RequestBody String refreshToken) {
        System.out.println("originURI : " + originURI);
        System.out.println("refreshToken : " + refreshToken);

        // TODO refreshToken 검증하는 로직 거쳐야 함. (refresh token 유효기간도 설정하고)

        return refreshToken;
    }


}
