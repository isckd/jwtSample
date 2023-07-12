package com.example.jwttest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Login 시 사용할 Dto
 */
public class LoginDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    public LoginDto(@NotNull @Size(min = 3, max = 50) String username, @NotNull @Size(min = 3, max = 100) String password) {
        this.username = username;
        this.password = password;
    }

    public LoginDto() {
    }

    public static LoginDtoBuilder builder() {
        return new LoginDtoBuilder();
    }

    public @NotNull @Size(min = 3, max = 50) String getUsername() {
        return this.username;
    }

    public @NotNull @Size(min = 3, max = 100) String getPassword() {
        return this.password;
    }

    public void setUsername(@NotNull @Size(min = 3, max = 50) String username) {
        this.username = username;
    }

    public void setPassword(@NotNull @Size(min = 3, max = 100) String password) {
        this.password = password;
    }

    public static class LoginDtoBuilder {
        private @NotNull @Size(min = 3, max = 50) String username;
        private @NotNull @Size(min = 3, max = 100) String password;

        LoginDtoBuilder() {
        }

        public LoginDtoBuilder username(@NotNull @Size(min = 3, max = 50) String username) {
            this.username = username;
            return this;
        }

        public LoginDtoBuilder password(@NotNull @Size(min = 3, max = 100) String password) {
            this.password = password;
            return this;
        }

        public LoginDto build() {
            return new LoginDto(username, password);
        }

        public String toString() {
            return "LoginDto.LoginDtoBuilder(username=" + this.username + ", password=" + this.password + ")";
        }
    }
}
