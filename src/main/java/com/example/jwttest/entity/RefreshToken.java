package com.example.jwttest.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


// redis key 는 refreshToken + @Id 로 정의된다.
// timeToLive : second 단위
@RedisHash(value = "refreshToken", timeToLive = 30)
public class RefreshToken {
    @Id
    private String username;

    private String refreshToken;

    public RefreshToken(final String refreshToken, final String username) {
        this.refreshToken = refreshToken;
        this.username = username;
    }

    public String getRefreshToken() { return refreshToken; }

    public String getUsername() {
        return username;
    }

}
