package com.example.jwttest.handler

import com.example.jwttest.dto.ErrorDto
import com.example.jwttest.exception.CustomException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseExceptionHandler : ResponseEntityExceptionHandler() {
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403, 권한 없음
    @ExceptionHandler(value = [CustomException::class])
    @ResponseBody
    protected fun forbidden(ex: RuntimeException, request: WebRequest?): ErrorDto {
        return ErrorDto(HttpStatus.FORBIDDEN.value(), ex.message)
    }
}