package com.example.jwttest.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomException extends AuthenticationException {
    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
