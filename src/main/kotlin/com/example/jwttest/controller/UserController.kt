package com.example.jwttest.controller

import com.example.jwttest.dto.UserDto
import com.example.jwttest.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(private val userService: UserService) {
    @GetMapping("/hello")
    fun returnHello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello World!")
    }

    @PostMapping("/test-redirect")
    @Throws(IOException::class)
    fun testRediredct(response: HttpServletResponse) {
        response.sendRedirect("/api/user")
    }

    @PostMapping("/signup")
    fun signup(
        @RequestBody userDto: @Valid UserDto?
    ): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.signup(userDto))
    }

    /**
     * 내 정보 조회
     * @PreAuthoize 어노테이션은 Controller 단에서 적용되어 AccessDeniedHandler 를 상속한 클래스에서 잡지 못한다.
     * 따라서, AccessDeniedException 을 커스텀하게 처리하기 위해선 SecurityConfig 에서 .hasRole() 속성으로 적용해야 한다.
     */
    @GetMapping("/user")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    fun getMyUserInfo(request: HttpServletRequest?): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.myUserWithAuthorities)
    }

    /**
     * 다른 유저 정보 조회 (ADMIN 권한 필요)
     */
    @GetMapping("/user/{username}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    fun getUserInfo(@PathVariable username: String?): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.myUserWithAuthorities)
    }
}