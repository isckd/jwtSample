package com.example.jwttest.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Login 시 사용할 Dto
 */
class LoginDto {
    var username: @NotNull @Size(min = 3, max = 50) String? = null
    var password: @NotNull @Size(min = 3, max = 100) String? = null

    constructor(
        username: @NotNull @Size(min = 3, max = 50) String?,
        password: @NotNull @Size(min = 3, max = 100) String?
    ) {
        this.username = username
        this.password = password
    }

    constructor()

    class LoginDtoBuilder internal constructor() {
        private var username: @NotNull @Size(min = 3, max = 50) String? = null
        private var password: @NotNull @Size(min = 3, max = 100) String? = null
        fun username(username: @NotNull @Size(min = 3, max = 50) String?): LoginDtoBuilder {
            this.username = username
            return this
        }

        fun password(password: @NotNull @Size(min = 3, max = 100) String?): LoginDtoBuilder {
            this.password = password
            return this
        }

        fun build(): LoginDto {
            return LoginDto(username, password)
        }

        override fun toString(): String {
            return "LoginDto.LoginDtoBuilder(username=" + username + ", password=" + password + ")"
        }
    }

    companion object {
        fun builder(): LoginDtoBuilder {
            return LoginDtoBuilder()
        }
    }
}