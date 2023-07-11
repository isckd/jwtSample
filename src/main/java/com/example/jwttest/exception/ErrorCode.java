package com.example.jwttest.exception;

public enum ErrorCode {
    INVALID_SIGNATURE("잘못된 JWT 서명"),
    EXPIRED_ACCESS_TOKEN("만료된 access 토큰"),
    NO_REFRESH_TOKEN("Refresh Token 없음! 주의 요망!!"),
    REFRESH_TOKEN_ERROR(" : Refresh Token 검증 시 에러 발생"),
    UNSUPPORTED_JWT_TOKEN("지원되지 않는 JWT 토큰"),
    INVALID_JWT_TOKEN("JWT 토큰이 잘못되었습니다."),
    DUPLICATE_USER("는 이미 가입되어 있는 유저"),
    USER_NOT_FOUND("USER not found");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
