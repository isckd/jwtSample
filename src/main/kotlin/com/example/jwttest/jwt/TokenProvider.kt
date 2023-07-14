package com.example.jwttest.jwt

import com.example.jwttest.dto.TokenDto
import com.example.jwttest.exception.CustomException
import com.example.jwttest.exception.ErrorCode
import com.example.jwttest.repository.RefreshTokenRepository
import com.example.jwttest.service.CustomUserDetailsService
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class TokenProvider(
    @param:Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.token-validity-in-seconds}") tokenValidityInSeconds: Long,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userDetailsService: UserDetailsService,
    private val customUserDetailsService: CustomUserDetailsService
) : InitializingBean {
    private val tokenValidityInMilliseconds: Long
    private var key: Key? = null

    init {
        tokenValidityInMilliseconds = tokenValidityInSeconds * 1000
    }

    /**
     * ${jwt.secret} 값을 Base64 Decode 하여 key 변수에 할당한다.
     */
    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val keyBytes = Decoders.BASE64.decode(secret) // Base64 Decode
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * Authentication 객체의 권한정보를 이용해서 토큰을 생성하는 메소드
     */
    fun createToken(authentication: Authentication): String {
        val authorities = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(","))
        val now = Date().time
        val validity = Date(now + tokenValidityInMilliseconds)
        return Jwts.builder()
            .setSubject(authentication.name)            // 사용자 이름
            .claim(AUTHORITIES_KEY, authorities)        // authorities : (ROLE_USER", ROLE_ADMIN) or (ROLE_USER)
            .signWith(key, SignatureAlgorithm.HS512)    // HMAC SHA512 알고리즘을 사용해서 sign
            .setExpiration(validity)                    // 토큰의 만료시간 설정
            .compact()                                  // 직렬화
    }

    /**
     * 토큰을 파라미터로 받아서 토큰에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메소드
     */
    fun getAuthentication(token: String?): Authentication {
        // 토큰을 이용해서 Claims 객체를 생성한다. 이 때 토큰 검증을 실시한다.
        val claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        // Claims 객체에서 권한 정보를 가져온다.
        val authorities: Collection<GrantedAuthority> = Arrays.stream(
            claims[AUTHORITIES_KEY].toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList())

        // 권한 정보를 이용해서 User 객체를 생성하고 리턴한다. 여기서 User 객체는 UserDetails 인터페이스를 구현한 객체이다. (User Entity 가 아님)
        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    /**
     * 토큰의 유효성(만료) 검증을 수행하는 메소드
     */
    @Throws(CustomException::class)
    fun validateToken(token: String?, refreshToken: String?): TokenDto {
        var refreshToken = refreshToken
        return try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            TokenDto(token, refreshToken)
        } catch (e: ExpiredJwtException) {
            log.info(e.message + " 만료된 access 토큰")
            val expiredTokenUsername = e.claims.subject
            val optionalRefreshToken = refreshTokenRepository.findById(expiredTokenUsername)
            if (!optionalRefreshToken.isPresent) {                                         // redis 에서 refresh token 을 찾을 수 없는 경우
                log.info("access token, refresh token 모두 만료되어 접근 제한")
                throw CustomException(ErrorCode.EXPIRED_ACCESS_REFRESH_TOKEN)
            }
            if (optionalRefreshToken.get().refreshToken == refreshToken) {                  // redis 에서 refresh token 을 찾고 주어진 토큰과 일치하는 경우
                log.info("access token 만료되었지만, refresh token 일치하여 재발급. 인증 정보 : '{}'", expiredTokenUsername)
                refreshToken = customUserDetailsService.deleteAndGenerateRefreshToken(expiredTokenUsername)         // RTS 전략 -> refresh token 사용 시 재발급
                TokenDto(createNewToken(expiredTokenUsername), refreshToken)
            } else {                                                                        // redis 에서 username 에 대한 refresh token 을 찾았지만 주어진 토큰과 일치하지 않는 경우
                throw CustomException(ErrorCode.REFRESH_TOKEN_ERROR)
            }
        } catch (e: UnsupportedJwtException) {
            throw CustomException(ErrorCode.UNSUPPORTED_JWT_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw CustomException(ErrorCode.INVALID_JWT_TOKEN)
        } catch (e: SecurityException) {
            throw CustomException(ErrorCode.INVALID_SIGNATURE)
        } catch (e: MalformedJwtException) {
            throw CustomException(ErrorCode.INVALID_SIGNATURE)
        }
    }

    /**
     * access token 만료, refresh token 일치 시
     *  -> access token 재발급하고, SecurityContext 에 저장
     */
    private fun createNewToken(expiredTokenUsername: String): String {
        val userDetails = userDetailsService.loadUserByUsername(expiredTokenUsername)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        return createToken(authentication)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TokenProvider::class.java)
        private const val AUTHORITIES_KEY = "auth"                          // 토큰에 담길 권한 정보의 Key (value : ROLE_USER, ROLE_ADMIN)
    }
}