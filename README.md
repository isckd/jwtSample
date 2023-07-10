# Jwt 토큰 샘플 프로젝트

## Springboot 2.6.5, Java 8

### master branch 현재 java 로 진행중

### 추후 kotlin 브랜치 새로 생성 예정 (kotlin 1.6.10)


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
    - 현재 토큰 유효기간 15초

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
    

- 아직 Handler 처리 다 안함. (예외처리 등)
- refresh token 처리 안함.

