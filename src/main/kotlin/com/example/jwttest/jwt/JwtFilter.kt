package com.example.jwttest.jwt

import com.example.jwttest.dto.TokenDto
import com.example.jwttest.exception.CustomException
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(private val tokenProvider: TokenProvider) : OncePerRequestFilter() {
    /**
     * 토큰의 인증정보를 SecurityContext 에 보내는 역할
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.debug("URI : " + httpServletRequest.requestURI)
        val tokenDto = resolveToken(httpServletRequest) // 토큰 정보를 꺼내온다.
        if (!tokenDto.isPresent) {
            filterChain.doFilter(httpServletRequest, httpServletResponse) // 토큰이 없으면 토큰 검증을 실시하지 않는다.
            return
        }
        try {
            val newTokens = tokenProvider.validateToken(tokenDto.get().token, tokenDto.get().refreshToken)
            processValidTokens(tokenDto.get().token, newTokens, httpServletRequest, httpServletResponse, filterChain)
        } catch (e: CustomException) {
            handleInvalidTokens(e, httpServletResponse)
        }
    }

    /**
     * Request Header 에서 토큰 정보를 꺼내오기 위한 메서드
     */
    private fun resolveToken(request: HttpServletRequest): Optional<TokenDto> {
        var accessToken = request.getHeader(AUTHORIZATION_HEADER) // 헤더에서 토큰 정보를 꺼내온다.
        val refreshToken = request.getHeader(REFRESH_TOKEN_HEADER)
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {            // 토큰 정보가 존재하고, Bearer로 시작하는 경우
            accessToken = accessToken.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].trim { it <= ' ' } // Bearer 다음 문자열을 반환한다.
            return Optional.of(TokenDto(accessToken, refreshToken))
        }
        return Optional.empty()
    }

    /**
     * 토큰이 유효하지 않을 때 처리하는 메서드
     */
    @Throws(IOException::class)
    private fun handleInvalidTokens(e: CustomException, response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        response.writer.write(String.format("{\"error\": \"%s\"}", e.message))
    }

    /**
     * 토큰이 유효할 때 SecurityContext 에 저장하는 메서드
     */
    @Throws(IOException::class, ServletException::class)
    private fun processValidTokens(
        accessToken: String?,
        newTokens: TokenDto?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        if (StringUtils.hasText(newTokens?.token)) {
            val authentication = tokenProvider.getAuthentication(newTokens?.token)
            SecurityContextHolder.getContext().authentication = authentication
            log.debug(authentication.name + " 의 접근 허용. Security Context 에 저장 완료")
            if (newTokens?.token != accessToken) {
                response.addHeader(AUTHORIZATION_HEADER, "Bearer " + newTokens?.token)
                response.addHeader(REFRESH_TOKEN_HEADER, newTokens?.refreshToken)
            }
        } else {
            log.debug("No valid JWT token")
        }
        chain.doFilter(request, response)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtFilter::class.java)
        const val AUTHORIZATION_HEADER = "Authorization" // 헤더의 키 값
        const val REFRESH_TOKEN_HEADER = "refreshToken" // refresh token 키 값
    }
}