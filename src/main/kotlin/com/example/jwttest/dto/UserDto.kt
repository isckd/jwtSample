package com.example.jwttest.dto

import com.example.jwttest.entity.User
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * 회원가입 시 사용할 Dto
 */

data class UserDto(
    @field:NotNull
    @field:Size(min = 3, max = 50)
    var username: String? = null,

    @field:NotNull
    @field:Size(min = 3, max = 100)
    var password: String? = null,

    @field:NotNull
    @field:Size(min = 3, max = 50)
    var nickname: String? = null,

    var authorityDtoSet: Set<AuthorityDto>? = null
) {

    /**
     * UserDto 를 User 엔티티로 변환하는 메서드
     */
    companion object {
        fun from(user: User): UserDto {
            return user.run {                               // user.run 은 run {} 안의 내용을 user 에 적용하고 user 를 반환한다.
                UserDto(
                    username = this.username,
                    nickname = nickname,
                    authorityDtoSet = user.authorities!!
                        .map { authority ->                             // .map 은 list 의 각 요소에 대해 {} 안의 내용을 적용하고 적용된 요소들을 list 로 반환한다.
                            AuthorityDto(authority.authorityName)
                        }
                        .toSet()
                )
            }
        }
    }
}