package com.example.jwttest.dto;

public class TokensDto {

    private String accessToken;

    private String refreshToken;

    public TokensDto() {}

    public TokensDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}


