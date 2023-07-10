package com.example.jwttest.exception;

/**
 * 중복된 회원가입 Exception
 */
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
