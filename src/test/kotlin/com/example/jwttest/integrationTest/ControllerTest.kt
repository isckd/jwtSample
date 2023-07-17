package com.example.jwttest.integrationTest

import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional                  // 테스트코드의 Transactional 은 다르게 동작한다.

class ControllerTest() {
    @Autowired
    lateinit var mvc: MockMvc
    @BeforeEach
    fun setup(testInfo: TestInfo) {
        println("========================================================================================================================================================================================================")
        println("========================================================================================================================================================================================================")
        println("Starting Test : " + testInfo.displayName)
    }

    @Test
    @DisplayName("hello api 테스트")
    @Throws(Exception::class)
    fun helloTest() {
        mvc.perform(MockMvcRequestBuilders.get("/api/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect { mvcResult: MvcResult ->
                (mvcResult.response.contentAsString == "Hello World!")
            }
    }

    @Test
    @DisplayName("signup api 테스트")
    @Throws(Exception::class)
    fun signupTest() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/signup")
                .content(
                    "{\n" +
                            "  \"username\": \"test\",\n" +
                            "  \"password\": \"test\",\n" +
                            "  \"nickname\": \"test_nickname\"\n" +
                            "}"
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
        mvc.perform(
            MockMvcRequestBuilders.post("/api/signup")
                .content(
                    ("{\n" +
                            "  \"username\": \"user1\",\n" +
                            "  \"password\": \"user1\",\n" +
                            "  \"nickname\": \"user1_nickname\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("는 이미 가입되어 있는 유저"))
    }

    @Test
    @DisplayName("authenticate api 테스트")
    @Throws(Exception::class)
    fun loginTest() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/authenticate")
                .content(
                    ("{\n" +
                            "  \"username\": \"test\",\n" +
                            "  \"password\": \"test\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(401))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("test -> DB 에서 찾을 수 없습니다."))
        mvc.perform(
            MockMvcRequestBuilders.post("/api/authenticate")
                .content(
                    ("{\n" +
                            "  \"username\": \"user1\",\n" +
                            "  \"password\": \"user1\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @DisplayName("test-redirect api 테스트")
    @Throws(Exception::class)
    fun testRedirectTest() {
        mvc.perform(MockMvcRequestBuilders.post("/api/test-redirect"))
            .andExpect(MockMvcResultMatchers.status().`is`(401))
        val mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/api/authenticate")
                .content(
                    ("{\n" +
                            "  \"username\": \"user1\",\n" +
                            "  \"password\": \"user1\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val accessToken = JSONObject(mvcResult.response.contentAsString).getString("token")
        val refreshToken = JSONObject(mvcResult.response.contentAsString).getString("refreshToken")
        println("accessToken : $accessToken\nrefreshToken : $refreshToken")
        mvc.perform(
            MockMvcRequestBuilders.post("/api/test-redirect")
                .header("Authorization", "Bearer $accessToken")
                .header("refreshToken", refreshToken)
        )
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.redirectedUrl("/api/user"))
    }

    @Test
    @DisplayName("user api 테스트")
    @Throws(Exception::class)
    fun UserAPITest() {
        mvc.perform(MockMvcRequestBuilders.get("/api/user"))
            .andExpect(MockMvcResultMatchers.status().`is`(401))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.error")
                    .value("Full authentication is required to access this resource")
            )
        val mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/api/authenticate")
                .content(
                    ("{\n" +
                            "  \"username\": \"user1\",\n" +
                            "  \"password\": \"user1\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val accessToken = JSONObject(mvcResult.response.contentAsString).getString("token")
        val refreshToken = JSONObject(mvcResult.response.contentAsString).getString("refreshToken")
        mvc.perform(
            MockMvcRequestBuilders.get("/api/user")
                .header("Authorization", "Bearer $accessToken")
                .header("refreshToken", refreshToken)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("user1_nickname"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorityDtoSet[0].authorityName").value("ROLE_USER"))
        mvc.perform(
            MockMvcRequestBuilders.get("/api/user")
                .header("Authorization", "Bearer $accessToken")
                .header("refreshToken", refreshToken)
        )
    }

    @Test
    @DisplayName("admin api 테스트")
    @Throws(Exception::class)
    fun AdminAPITest() {
        mvc.perform(MockMvcRequestBuilders.get("/api/user/user1"))
            .andExpect(MockMvcResultMatchers.status().`is`(401))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.error")
                    .value("Full authentication is required to access this resource")
            )
        val mvcResult = mvc.perform(
            MockMvcRequestBuilders.post("/api/authenticate")
                .content(
                    ("{\n" +
                            "  \"username\": \"user1\",\n" +
                            "  \"password\": \"user1\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val accessToken = JSONObject(mvcResult.response.contentAsString).getString("token")
        val refreshToken = JSONObject(mvcResult.response.contentAsString).getString("refreshToken")
        mvc.perform(
            MockMvcRequestBuilders.get("/api/user/user1")
                .header("Authorization", "Bearer $accessToken")
                .header("refreshToken", refreshToken)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("권한이 없습니다."))
        val mvcResultAdmin = mvc.perform(
            MockMvcRequestBuilders.post("/api/authenticate")
                .content(
                    ("{\n" +
                            "  \"username\": \"admin\",\n" +
                            "  \"password\": \"admin\"\n" +
                            "}")
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val accessTokenAdmin = JSONObject(mvcResultAdmin.response.contentAsString).getString("token")
        val refreshTokenAdmin = JSONObject(mvcResultAdmin.response.contentAsString).getString("refreshToken")
        mvc.perform(
            MockMvcRequestBuilders.get("/api/user/user1")
                .header("Authorization", "Bearer $accessTokenAdmin")
                .header("refreshToken", refreshTokenAdmin)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("user1_nickname"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorityDtoSet[0].authorityName").value("ROLE_USER"))
        mvc.perform(
            MockMvcRequestBuilders.get("/api/user/admin")
                .header("Authorization", "Bearer $accessTokenAdmin")
                .header("refreshToken", refreshTokenAdmin)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("admin"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("admin"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorityDtoSet[0].authorityName").value("ROLE_ADMIN"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.authorityDtoSet[1].authorityName").value("ROLE_USER"))
    }
}