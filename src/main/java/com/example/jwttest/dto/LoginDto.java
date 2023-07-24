package com.example.jwttest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String userId;

    @NotNull
    @Size(min = 3, max = 100)
    private String ci;

    public LoginDto(@NotNull @Size(min = 3, max = 50) String userId, @NotNull @Size(min = 3, max = 100) String ci) {
        this.userId = userId;
        this.ci = ci;
    }

    public LoginDto() {}

    public @NotNull @Size(min = 3, max = 50) String getUserId() {
        return this.userId;
    }

    public @NotNull @Size(min = 3, max = 100) String getCi() {
        return this.ci;
    }
}
