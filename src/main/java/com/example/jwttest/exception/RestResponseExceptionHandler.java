package com.example.jwttest.exception;

import com.example.jwttest.dto.ErrorDto;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(UNAUTHORIZED)                                           // 401
    @ExceptionHandler(value = {CustomException.class})
    @ResponseBody
    protected ErrorDto unAutorized(CustomException e, WebRequest request) {
        return new ErrorDto(UNAUTHORIZED.value(), e.getMessage());
    }
}
