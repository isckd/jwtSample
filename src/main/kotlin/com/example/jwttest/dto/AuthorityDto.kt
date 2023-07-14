package com.example.jwttest.dto

import com.example.jwttest.entity.Authority

data class AuthorityDto(
    var authorityName: String?
){

    /**
     * AuthorityDto 를 Authority 엔티티로 변환하는 메서드
     * 현재는 안쓰인다.
     */
    companion object {
        fun from(authority: Authority): AuthorityDto {
            return authority.run {
                AuthorityDto(
                    authorityName = authorityName
                )
            }
        }
    }
}