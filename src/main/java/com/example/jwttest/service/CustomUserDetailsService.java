package com.example.jwttest.service;

import com.example.jwttest.entity.RefreshToken;
import com.example.jwttest.entity.User;
import com.example.jwttest.repository.RefreshTokenRepository;
import com.example.jwttest.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    public CustomUserDetailsService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    /**
     * DB 에서 유저 정보를 가져온다.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> DB 에서 찾을 수 없습니다."));
    }

    /**
     * 유저 정보를 기반으로 UserDetails 객체를 생성한다.
     */
    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }

    /**
     * refresh 토큰 생성
     */
    public String generateRefreshToken(String username) {
        RefreshToken refreshTokenObject = new RefreshToken(UUID.randomUUID().toString(), username);
        refreshTokenRepository.save(refreshTokenObject);
        return refreshTokenObject.getRefreshToken();
    }

    /**
     * refresh 토큰 삭제 후 재발급
     */
    @Transactional
    public String deleteAndGenerateRefreshToken(String username) {
        refreshTokenRepository.deleteById(username);
        return generateRefreshToken(username);
    }
}
