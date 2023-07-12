package com.example.jwttest.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


/** redis key 는 refreshToken + @Id 로 정의된다.
 timeToLive : second 단위
*/

/**
 * 주의 !!  @RedisHash 로 ttl 을 설정하면 만료 시 데이터는 삭제되지만, key 값을 저장하는 set element 는 삭제되지 않는다.
 * 이로 인해 쓰레기 값이 계속 발생할 수 있음에 대비해야 한다.
 */
@RedisHash(value = "refreshToken", timeToLive = 10)
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
