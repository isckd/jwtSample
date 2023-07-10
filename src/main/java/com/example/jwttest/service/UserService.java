package com.example.jwttest.service;

import com.example.jwttest.dto.UserDto;
import com.example.jwttest.entity.Authority;
import com.example.jwttest.entity.User;
import com.example.jwttest.repository.UserRepository;
import com.example.jwttest.util.SecutiryUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

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
     * 회원가입
     */
    @Transactional
    public User singup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException(userDto.getUsername() + "는 이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")                         // 회원가입을 통한 유저는 권한이 USER
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))      // 권한은 한개만 준다.
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * 유저정보 조회
     */
    @Transactional
    public Optional<User> getMyUserWithAuthorities() {
        // getCurrentUsername() 의 리턴값이 Optional 이므로 flatMap 으로 꺼낸다.
        return SecutiryUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }

    /**
     * 내 정보 조회
     */
    @Transactional
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

}
