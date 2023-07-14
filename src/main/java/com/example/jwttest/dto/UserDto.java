package com.example.jwttest.dto;


import com.example.jwttest.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 회원가입 시 사용할 Dto
 */
public class UserDto {

    @NotNull
    @Size(min = 3,max = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3,max = 50)
    private String password;

    @NotNull
    @Size(min = 3,max = 50)
    private String nickname;

    private Set<AuthorityDto> authorityDtoSet;

    public UserDto(@NotNull @Size(min = 3, max = 50) String username, @NotNull @Size(min = 3, max = 50) String password, @NotNull @Size(min = 3, max = 50) String nickname, Set<AuthorityDto> authorityDtoSet) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authorityDtoSet = authorityDtoSet;
    }

    public UserDto() {
    }


    /**
     * User 엔티티를 UserDto 로 변환
     */
    public static UserDto from(User user) {
        if(user == null) return null;

        return UserDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public @NotNull @Size(min = 3, max = 50) String getUsername() {
        return this.username;
    }

    public @NotNull @Size(min = 3, max = 50) String getPassword() {
        return this.password;
    }

    public @NotNull @Size(min = 3, max = 50) String getNickname() {
        return this.nickname;
    }

    public Set<AuthorityDto> getAuthorityDtoSet() {
        return this.authorityDtoSet;
    }

    public void setUsername(@NotNull @Size(min = 3, max = 50) String username) {
        this.username = username;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setPassword(@NotNull @Size(min = 3, max = 50) String password) {
        this.password = password;
    }

    public void setNickname(@NotNull @Size(min = 3, max = 50) String nickname) {
        this.nickname = nickname;
    }

    public void setAuthorityDtoSet(Set<AuthorityDto> authorityDtoSet) {
        this.authorityDtoSet = authorityDtoSet;
    }

    public static class UserDtoBuilder {
        private @NotNull @Size(min = 3, max = 50) String username;
        private @NotNull @Size(min = 3, max = 50) String password;
        private @NotNull @Size(min = 3, max = 50) String nickname;
        private Set<AuthorityDto> authorityDtoSet;

        UserDtoBuilder() {
        }

        public UserDtoBuilder username(@NotNull @Size(min = 3, max = 50) String username) {
            this.username = username;
            return this;
        }

        public UserDtoBuilder password(@NotNull @Size(min = 3, max = 50) String password) {
            this.password = password;
            return this;
        }

        public UserDtoBuilder nickname(@NotNull @Size(min = 3, max = 50) String nickname) {
            this.nickname = nickname;
            return this;
        }

        public UserDtoBuilder authorityDtoSet(Set<AuthorityDto> authorityDtoSet) {
            this.authorityDtoSet = authorityDtoSet;
            return this;
        }

        public UserDto build() {
            return new UserDto(username, password, nickname, authorityDtoSet);
        }

        public String toString() {
            return "UserDto.UserDtoBuilder(username=" + this.username + ", password=" + this.password + ", nickname=" + this.nickname + ", authorityDtoSet=" + this.authorityDtoSet + ")";
        }
    }
}
