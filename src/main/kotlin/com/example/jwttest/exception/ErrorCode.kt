package com.example.jwttest.exception

enum class ErrorCode(val message: String) {
    INVALID_SIGNATURE("잘못된 JWT 서명"), EXPIRED_ACCESS_REFRESH_TOKEN("access, refresh 토큰 둘 다 만료됨"), NO_REFRESH_TOKEN("Refresh Token 없음! 주의 요망!!"), REFRESH_TOKEN_ERROR(
        " : Refresh Token 이 일치하지 않음"
    ),
    UNSUPPORTED_JWT_TOKEN("지원되지 않는 JWT 토큰"), INVALID_JWT_TOKEN("JWT 토큰이 잘못되었습니다."), DUPLICATE_USER("는 이미 가입되어 있는 유저"), USER_NOT_FOUND(
        "USER not found"
    )

}