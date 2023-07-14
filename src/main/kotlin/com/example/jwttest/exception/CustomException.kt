package com.example.jwttest.exception

class CustomException(errorCode: ErrorCode) : RuntimeException(
    errorCode.message
)