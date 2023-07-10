package com.example.jwttest.controller;

import com.example.jwttest.dto.UserDto;
import com.example.jwttest.entity.User;
import com.example.jwttest.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.ws.Response;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@Log4j2
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/test-redirect")
    public void testRediredct(HttpServletResponse response) throws IOException{
        response.sendRedirect("/api/user");
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.singup(userDto));
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")        // USER, ADMIN 권한 접근 가능
    public ResponseEntity<User> getMyUserInfo(HttpServletRequest request) {
        log.info(request.getHeader("Authorization"));       // 헤더 잘 나오는지
        return ResponseEntity.ok(userService.getMyUserWithAuthorities().get());
    }

    /**
     * 다른 유저 정보 조회 (ADMIN 권한 필요)
     */
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")                // ADMIN 권한만 접근 가능
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username).get());
    }
}
