package com.example.jwttest.service

import com.example.jwttest.entity.Authority
import com.example.jwttest.entity.RefreshToken
import com.example.jwttest.entity.User
import com.example.jwttest.repository.RefreshTokenRepository
import com.example.jwttest.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors

@Component
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) : UserDetailsService {

    /**
     * DB 에서 유저 정보를 가져온다.
     */
    @Transactional
    @Throws(IllegalArgumentException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findOneWithAuthoritiesByUsername(username)
            .map { user: User? -> createUser(username, user) }
            .orElseThrow { IllegalArgumentException("$username -> DB 에서 찾을 수 없습니다.") }
    }

    /**
     * 유저 정보를 기반으로 UserDetails 객체를 생성한다.
     */
    private fun createUser(username: String, user: User?): org.springframework.security.core.userdetails.User {
        if (!user!!.isActivated) {
            throw RuntimeException("$username -> 활성화되어 있지 않습니다.")
        }
        val grantedAuthorities = user.authorities?.stream()                                         // user 엔티티의 권한을 가져온다.
            ?.map { authority: Authority? -> SimpleGrantedAuthority(authority?.authorityName) }     // SimpleGrantedAuthority 객체로 반환
            ?.collect(Collectors.toList())
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            grantedAuthorities                                                                      // UserDetails 객체에 권한을 넣어준다.
        )
    }

    /**
     * refresh 토큰 생성
     */
    fun generateRefreshToken(username: String?): String? {
        val refreshTokenObject = RefreshToken(UUID.randomUUID().toString(), username)
        refreshTokenRepository.save(refreshTokenObject)
        return refreshTokenObject.refreshToken
    }

    /**
     * refresh 토큰 삭제 후 재발급
     */
    @Transactional
    fun deleteAndGenerateRefreshToken(username: String): String? {
        refreshTokenRepository.deleteById(username)
        return generateRefreshToken(username)
    }
}