package com.example.jwttest.exception;

/**
 * username 못찾을 때 Exception
 */
public class NotFoundMemberException extends RuntimeException {
    public NotFoundMemberException() {
        super();
    }

    public NotFoundMemberException(String message, Throwable cause) {
        super(message);
    }

    public NotFoundMemberException(String message) {
        super(message);
    }

    public NotFoundMemberException(Throwable cause) {
        super(cause);
    }
}
