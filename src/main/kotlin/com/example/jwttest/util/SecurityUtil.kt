package com.example.jwttest.util

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

object SecurityUtil {
    private val log = LoggerFactory.getLogger(SecurityUtil::class.java)
    val currentUsername: Optional<String>
        /**
         * SecurityContext 의 Authentication 객체를 이용해 username 을 리턴하는 메소드 <br></br>
         * Security Context 에 Authentication 객체가 저장되는 시점은 JwtFilter 의 doFilter 메서드에서 <br></br>
         * Request 가 들어올 때 Security Context 에 Authentication 객체를 저장해서 사용한다.
         */
        get() {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication == null) {
                log.debug("Security Context에 인증 정보가 없습니다.")
                return Optional.empty()
            }
            var username: String? = null
            if (authentication.principal is UserDetails) {
                val springSecurityUser = authentication.principal as UserDetails
                username = springSecurityUser.username
            } else if (authentication.principal is String) {
                username = authentication.principal as String
            }
            return Optional.ofNullable(username)
        }
}