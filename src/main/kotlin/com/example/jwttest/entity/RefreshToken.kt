package com.example.jwttest.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

/** redis key 는 refreshToken + @Id 로 정의된다.
 * timeToLive : second 단위
 */
/**
 * 주의 !!  @RedisHash 로 ttl 을 설정하면 만료 시 데이터는 삭제되지만, key 값을 저장하는 set element 는 삭제되지 않는다.
 * 이로 인해 쓰레기 값이 계속 발생할 수 있음에 대비해야 한다.
 *       --> 그런데 생각해보면 key 값에는 username 만 들어가는데,
 *       유저 한 명당 username 은 한 개이므로 굳이 삭제해야 될 필요가 있을까? 이는 고민이 필요한 부분이다.
 */
@RedisHash(value = "refreshToken", timeToLive = 10)
class RefreshToken(
    val refreshToken: String,
    @field:Id val username: String?
)