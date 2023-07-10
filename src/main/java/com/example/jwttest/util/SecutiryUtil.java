package com.example.jwttest.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Log4j2
public class SecutiryUtil {


    /**
     * SecurityContext 의 Authentication 객체를 이용해 username 을 리턴하는 메소드 <br>
     * Security Context 에 Authentication 객체가 저장되는 시점은 JwtFilter 의 doFilter 메서드에서 <br>
     * Request 가 들어올 때 Security Context 에 Authentication 객체를 저장해서 사용한다.
     */
    public static Optional<String> getCurrentUsername() {
       final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

       if(authentication == null) {
           log.debug("Security Context에 인증 정보가 없습니다.");
           return Optional.empty();
       }

       String username = null;
       if (authentication.getPrincipal() instanceof UserDetails) {
           UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
           username = springSecurityUser.getUsername();
       } else if (authentication.getPrincipal() instanceof String) {
           username = (String) authentication.getPrincipal();
       }
       return Optional.ofNullable(username);
    }
}
