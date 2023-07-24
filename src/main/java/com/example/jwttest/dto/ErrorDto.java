package com.example.jwttest.dto;

public class ErrorDto {

    private final int status;
    private final String message;

    public ErrorDto(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }
}
