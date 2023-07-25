package com.example.jwttest.exception;

public enum ErrorCode {
    EXPIRED_JWT_TOKEN("만료된 JWT Token"),
    INVALID_USER("Invalid userId, ci"),
    UNSUPPORTED_TOKEN("지원되지 않는 토큰"),
    INVALID_JWT_TOKEN("잘못된 JWT Token"),
    INVALID_SIGNATURE("잘못된 JWT 서명");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
