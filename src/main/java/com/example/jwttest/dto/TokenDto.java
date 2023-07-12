package com.example.jwttest.dto;


/**
 * Response 에 사용할 Token Dto
 */
public class TokenDto {

    private String token;

    private String refreshToken;

    public TokenDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public TokenDto() {
    }

    public static TokenDtoBuilder builder() {
        return new TokenDtoBuilder();
    }

    public String getToken() {
        return this.token;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class TokenDtoBuilder {
        private String token;
        private String refreshToken;

        TokenDtoBuilder() {
        }

        public TokenDtoBuilder token(String token) {
            this.token = token;
            return this;
        }

        public TokenDtoBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public TokenDto build() {
            return new TokenDto(token, refreshToken);
        }

        public String toString() {
            return "TokenDto.TokenDtoBuilder(token=" + this.token + ", refreshToken=" + this.refreshToken + ")";
        }
    }
}
