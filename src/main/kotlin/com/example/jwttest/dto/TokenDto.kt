package com.example.jwttest.dto

/**
 * Response 에 사용할 Token Dto
 */
class TokenDto {
    var token: String? = null
    var refreshToken: String? = null

    constructor(token: String?, refreshToken: String?) {
        this.token = token
        this.refreshToken = refreshToken
    }

    constructor()

    class TokenDtoBuilder internal constructor() {
        private var token: String? = null
        private var refreshToken: String? = null
        fun token(token: String?): TokenDtoBuilder {
            this.token = token
            return this
        }

        fun refreshToken(refreshToken: String?): TokenDtoBuilder {
            this.refreshToken = refreshToken
            return this
        }

        fun build(): TokenDto {
            return TokenDto(token, refreshToken)
        }

        override fun toString(): String {
            return "TokenDto.TokenDtoBuilder(token=" + token + ", refreshToken=" + refreshToken + ")"
        }
    }

    companion object {
        fun builder(): TokenDtoBuilder {
            return TokenDtoBuilder()
        }
    }
}