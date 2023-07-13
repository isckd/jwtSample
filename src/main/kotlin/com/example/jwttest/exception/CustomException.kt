package com.example.jwttest.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(
    errorCode.message
)