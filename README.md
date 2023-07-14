# Jwt 토큰 샘플 프로젝트

## Springboot 2.6.5, Java 8 (Compile), Kotlin 1.6.10

##  로컬에 Redis 설치해서 띄워야 함

### 2023/07/30
- jwt 토큰 검증 및 생성 로직 구현
- 관련 API 사용방법
  - /api/signup (POST, 계정 생성)
    - request body
      - username
      - password
      - nickname

  - /api/authenticate (POST, 로그인)
    - request body
      - username
      - password
    - Tests (Postman의 탭)
      - var jsonData = JSON.parse(responseBody)
        pm.globals.set("jwt_tutorial_token", jsonData.token);
      - 위의 코드를 Tests 탭에 넣어주면, jwt_tutorial_token 이라는 변수에 토큰이 저장됨.
    - 현재 토큰 유효기간 5초

  - /api/test-redirect (POST, 새로고침)
    - 헤더를 토큰으로 받음
    - postman 의 Authorization 탭에서 Type 을 Bearer Token 으로 설정하고, Token 에 {{jwt_tutorial_token}} 을 입력하면 됨.
    - 로그인(authenticate) 이후 새로고침 할 때, /api/user 로 redirect 됨. 잘 되는지 테스트해보기.

  - /api/user (GET, 사용자 정보 조회 - ROLE_USER 을 포함하면 가능)
    - 헤더를 토큰으로 받음
    - postman 의 Authorization 탭에서 Type 을 Bearer Token 으로 설정하고, Token 에 {{jwt_tutorial_token}} 을 입력하면 됨.
    - request body X

  - /api/user/{username} (GET, 관리자 정보 조회 - ROLE_ADMIN 만 가능)
    - 헤더를 토큰으로 받음
    - Admin 계정 생성하는 API 는 없으므로 직접 DB에 넣고 해야 함.
    - postman 의 Authorization 탭에서 Type 을 Bearer Token 으로 설정하고, Token 에 {{jwt_tutorial_token}} 을 입력하면 됨.


## flow (핵심 로직만)
로그인(/authenticate) 시 access token, refresh token 둘 다 발급 (refresh token 은 redis 에 저장)
- access token 만료 시간 5초, refresh token 만료 시간 10초 (테스트용이므로 짧게)
- access token HS256 알고리즘으로 검증 
  - 인증 실패 시 401 에러
  - 인증 성공 시 200
  - 만료된 access token 시 refresh token 검증
    - refresh token 검증 성공 시 access token 재발급 (Refresh token Rotation 전략으로 refresh token 도 재발급)
    - refresh token 검증 실패 시 에러


- 테스트코드 추가해야 함.
  
문제 제기 : 과연 Redis 가 맞는것인가?
- 회원이 1000만 명이라면? 1000만 개의 refresh token 을 저장해야 하는가?

추가 개발하면 좋을 것 같은 것들
- HS256 말고 RSA 암호화 알고리즘 사용? -> 다만 웹/앱 단에서도 공개키를 사용할 수 있어야 함. (과연 실무 협의가 될지..)