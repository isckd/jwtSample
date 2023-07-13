package com.example.jwttest.dto

import com.example.jwttest.entity.Authority

data class AuthorityDto(
    var authorityName: String? = null
){
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