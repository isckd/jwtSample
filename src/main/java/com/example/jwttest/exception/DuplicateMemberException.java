package com.example.jwttest.exception;

import org.springframework.security.core.parameters.P;

public class DuplicateMemberException extends RuntimeException {

    public DuplicateMemberException() {
        super();
    }

    public DuplicateMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMemberException(String message) {
        super(message);
    }

    public DuplicateMemberException(Throwable cause) {
        super(cause);
    }
}
