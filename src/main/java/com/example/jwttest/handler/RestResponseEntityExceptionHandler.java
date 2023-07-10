package com.example.jwttest.handler;

import com.example.jwttest.dto.ErrorDto;
import com.example.jwttest.exception.DuplicateMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.CONFLICT;
@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)    // 409, 중복
    @ExceptionHandler(value = { DuplicateMemberException.class })
    @ResponseBody
    protected ErrorDto badRequest(RuntimeException ex, WebRequest request) {
        return new ErrorDto(CONFLICT.value(), ex.getMessage());
    }
}
