package com.example.jwttest.jwt

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 인증이 실패했을 때 처리할 클래스
 * Spring Security 는 Servlet Context 와는 다른 곳에서 동작하기 때문에 Exception 처리를 따로 해주어야 함!!
 */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.debug("Exception : {}", authException.message)

        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)
    }
}