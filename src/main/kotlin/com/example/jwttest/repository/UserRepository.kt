package com.example.jwttest.repository

import com.example.jwttest.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User?, Long?> {
    /**
     * username 을 기준으로 User 정보를 가져올 때 권한 정보도 같이 가져온다.
     */
    @EntityGraph(attributePaths = ["authorities"])          // authorities 를 eager 조회로 같이 가져온다.
    fun findOneWithAuthoritiesByUsername(username: String): Optional<User>
}