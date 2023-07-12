package com.example.jwttest.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "`user`")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    public User(Long userId, String username, String password, String nickname, boolean activated, Set<Authority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.activated = activated;
        this.authorities = authorities;
    }

    public User() {
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public Set<Authority> getAuthorities() {
        return this.authorities;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public static class UserBuilder {
        private Long userId;
        private String username;
        private String password;
        private String nickname;
        private boolean activated;
        private Set<Authority> authorities;

        UserBuilder() {
        }

        public UserBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public UserBuilder activated(boolean activated) {
            this.activated = activated;
            return this;
        }

        public UserBuilder authorities(Set<Authority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public User build() {
            return new User(userId, username, password, nickname, activated, authorities);
        }

        public String toString() {
            return "User.UserBuilder(userId=" + this.userId + ", username=" + this.username + ", password=" + this.password + ", nickname=" + this.nickname + ", activated=" + this.activated + ", authorities=" + this.authorities + ")";
        }
    }
}
