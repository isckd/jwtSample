package com.example.jwttest.jwt

import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * "인가" 예외 처리. (403 Forbidden)
 * 권한이 맞지 않을 때 처리
 */
@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {

    private val log = LoggerFactory.getLogger(javaClass)
    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN;
        response.contentType = "application/json";
        response.writer.write(String.format("{\"error\": \"%s\"}", "권한이 없습니다."))
        log.error("인가 거부. 권한이 올바르지 않음!!");
    }
}