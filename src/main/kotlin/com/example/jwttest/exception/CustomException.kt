package com.example.jwttest.exception

import org.springframework.security.core.AuthenticationException

class CustomException(errorCode: ErrorCode) : AuthenticationException(
    errorCode.message
)