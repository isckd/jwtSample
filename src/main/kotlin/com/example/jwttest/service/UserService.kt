package com.example.jwttest.service

import com.example.jwttest.dto.UserDto
import com.example.jwttest.dto.UserDto.Companion.from
import com.example.jwttest.entity.Authority
import com.example.jwttest.entity.User
import com.example.jwttest.exception.CustomException
import com.example.jwttest.exception.ErrorCode
import com.example.jwttest.repository.UserRepository
import com.example.jwttest.util.SecurityUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 회원가입, 유저정보 조회
 */
@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {
    /**
     * 회원가입
     */
    @Transactional
    fun signup(userDto: UserDto?): UserDto? {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto?.username!!).orElse(null) != null) {
            throw CustomException(ErrorCode.DUPLICATE_USER)
        }
        val authority = Authority(authorityName = "ROLE_USER")
        val user = User(
            username = userDto.username,
            password = passwordEncoder.encode(userDto.password),
            nickname = userDto.nickname,
            authorities = setOf(authority),
            isActivated = true
        )

        return from(userRepository.save(user))
    }

    /**
     * 내 정보 조회 (ROLE_USER)
     */
    @Transactional(readOnly = true)
    fun getUserWithAuthorities(username: String): UserDto {
        return from(
            userRepository.findOneWithAuthoritiesByUsername(username)
                .orElse(null)
        )
    }

    /**
     * 유저정보 조회 (ROLE_ADMIN)
     */
    @get:Transactional(readOnly = true)
    val myUserWithAuthorities: UserDto
        get() = from(
            SecurityUtil.currentUsername                                                                                // SecurityContext 에서 username 을 가져온다.
                .flatMap { username: String -> userRepository.findOneWithAuthoritiesByUsername(username)  }            // username 을 기준으로 User 정보를 가져온다.
                .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
        )
}