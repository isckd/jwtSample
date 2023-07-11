package com.example.jwttest.controller;

import com.example.jwttest.dto.LoginDto;
import com.example.jwttest.dto.TokenDto;
import com.example.jwttest.entity.RefreshToken;
import com.example.jwttest.jwt.JwtFilter;
import com.example.jwttest.jwt.TokenProvider;
import com.example.jwttest.repository.RefreshTokenRepository;
import com.example.jwttest.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserService userService;


    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }


    /**
     * 로그인으로 토큰을 발급받는다.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        RefreshToken refreshToken = userService.generateRefreshToken(loginDto.getUsername());
        refreshTokenRepository.save(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);   // 헤더에 토큰을 넣는다.
        httpHeaders.add("refreshToken", refreshToken.getRefreshToken());

        return new ResponseEntity<>(new TokenDto(jwt, refreshToken.getRefreshToken()), httpHeaders, HttpStatus.OK);     // 임시로 바디에도 넣는다.
    }
}
