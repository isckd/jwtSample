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

/**
 * 토큰의 인증정보를 SecurityContext 에 보내는 역할
 * OncePerRequestFilter : 모든 요청에 대해 단 한번만 필터링을 수행한다. (기본 서블릿 필터는 DispatcherServlet 을 들어가고 나올 때마다 수행된다.)
 */
class JwtFilter(
        private val tokenProvider: TokenProvider,
        private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.debug("URI : " + httpServletRequest.requestURI)
        val tokenDto = resolveToken(httpServletRequest)                   // 토큰 정보를 꺼내온다.
        if (!tokenDto.isPresent) {
            filterChain.doFilter(httpServletRequest, httpServletResponse) // 토큰이 없으면 토큰 검증을 실시하지 않는다.
            return
        }
        try {
            val newTokens = tokenProvider.validateToken(tokenDto.get().token, tokenDto.get().refreshToken)
            processValidTokens(tokenDto.get().token, newTokens, httpServletRequest, httpServletResponse, filterChain)
        } catch (e: CustomException) {
            jwtAuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, e)
        }
    }

    /**
     * Request Header 에서 토큰 정보를 꺼내오기 위한 메서드
     */
    private fun resolveToken(request: HttpServletRequest): Optional<TokenDto> {
        var accessToken = request.getHeader(AUTHORIZATION_HEADER)                                     // 헤더에서 토큰 정보를 꺼내온다.
        val refreshToken = request.getHeader(REFRESH_TOKEN_HEADER)
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {            // 토큰 정보가 존재하고, Bearer로 시작하는 경우 (RFC 6570)
            accessToken = accessToken.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1].trim { it <= ' ' }                                                 // Bearer 다음 문자열을 반환한다.
            return Optional.of(TokenDto(accessToken, refreshToken))
        }
        return Optional.empty()
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
            log.debug(authentication.name + " 의 인가 허용")
            if (newTokens?.token != accessToken) {                                              // accessToken 이 만료되어서 재발급된 경우
                log.debug("new Access token 발급해 Security Context 에 '{}' 인증 정보를 저장. 인증 정보 : {}", authentication.name, authentication.authorities)
                response.addHeader(AUTHORIZATION_HEADER, "Bearer " + newTokens?.token)
                response.addHeader(REFRESH_TOKEN_HEADER, newTokens?.refreshToken)
            }
        } else {
            log.debug("유효한 토큰이 없습니다. Security Context 에 저장하지 않음")
        }
        chain.doFilter(request, response)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtFilter::class.java)
        const val AUTHORIZATION_HEADER = "Authorization" // 헤더의 키 값
        const val REFRESH_TOKEN_HEADER = "refreshToken" // refresh token 키 값
    }
}