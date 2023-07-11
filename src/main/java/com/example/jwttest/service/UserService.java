package com.example.jwttest.service;

import com.example.jwttest.dto.LoginDto;
import com.example.jwttest.dto.UserDto;
import com.example.jwttest.entity.Authority;
import com.example.jwttest.entity.RefreshToken;
import com.example.jwttest.entity.User;
import com.example.jwttest.exception.DuplicateMemberException;
import com.example.jwttest.exception.NotFoundMemberException;
import com.example.jwttest.repository.UserRepository;
import com.example.jwttest.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collections;
import java.util.UUID;

/**
 * 회원가입, 유저정보 조회
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * refresh 토큰 생성
     */
    @Transactional(readOnly = true)
    public RefreshToken generateRefreshToken(String username) {
        return new RefreshToken(UUID.randomUUID().toString(), username);
    }

    /**
     * 회원가입
     */
    @Transactional
    public UserDto singup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException(userDto.getUsername() + "는 이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")                         // 회원가입을 통한 유저는 권한이 USER
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }

    /**
     * 내 정보 조회 (ROLE_USER)
     */
    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
//        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
        return UserDto.from(
                userRepository.findOneWithAuthoritiesByUsername(username)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }

    /**
     * 유저정보 조회 (ROLE_ADMIN)
     */
    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()       // SecurityContext 에서 username 을 가져온다.
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)      // username 을 기준으로 User 정보를 가져온다.
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))     // 유저 정보가 없으면 에러
        );
    }



}
