package com.example.jwttest.controller;

import com.example.jwttest.dto.UserDto;
import com.example.jwttest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> returnHello() {
        return ResponseEntity.ok("Hello World!");
    }

    @PostMapping("/test-redirect")
    public void testRediredct(HttpServletResponse response) throws IOException{
        response.sendRedirect("/api/user");
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.signup(userDto));
    }

    /**
     * 내 정보 조회
     * @PreAuthoize 어노테이션은 Controller 단에서 적용되어 AccessDeniedHandler 를 상속한 클래스에서 잡지 못한다.
     * 따라서, AccessDeniedException 을 커스텀하게 처리하기 위해선 SecurityConfig 에서 .hasRole() 속성으로 적용해야 한다.
     */
    @GetMapping("/user")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDto> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
    }

    /**
     * 다른 유저 정보 조회 (ADMIN 권한 필요)
     */
    @GetMapping("/user/{username}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username));
    }
}
