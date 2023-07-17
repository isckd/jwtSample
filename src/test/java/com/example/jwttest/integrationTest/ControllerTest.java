package com.example.jwttest.integrationTest;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional                      // 테스트코드의 Transactional 은 다르게 동작한다. 모르면 검색하고 오자.
public class ControllerTest {

    @Autowired
    MockMvc mvc;

    @BeforeEach
    public void setup(TestInfo testInfo) {
        System.out.println("========================================================================================================================================================================================================");
        System.out.println("========================================================================================================================================================================================================");
        System.out.println("Starting Test : " + testInfo.getDisplayName());
    }


    @Test
    @DisplayName("hello api 테스트")
    public void helloTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Hello World!"));
    }

    @Test
    @DisplayName("signup api 테스트")
    public void signupTest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/signup")
                    .content("{\n" +
                                "  \"username\": \"test\",\n" +
                                "  \"password\": \"test\",\n" +
                                "  \"nickname\": \"test_nickname\"\n" +
                                "}")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/api/signup")
                .content("{\n" +
                        "  \"username\": \"user1\",\n" +
                        "  \"password\": \"user1\",\n" +
                        "  \"nickname\": \"user1_nickname\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(403))
            .andExpect(jsonPath("$.message").value("는 이미 가입되어 있는 유저"));
    }

    @Test
    @DisplayName("authenticate api 테스트")
    public void loginTest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/authenticate")
                        .content("{\n" +
                                "  \"username\": \"test\",\n" +
                                "  \"password\": \"test\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("test -> DB 에서 찾을 수 없습니다."));

        mvc.perform(MockMvcRequestBuilders.post("/api/authenticate")
                .content("{\n" +
                        "  \"username\": \"user1\",\n" +
                        "  \"password\": \"user1\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test-redirect api 테스트")
    public void testRedirectTest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/test-redirect"))
                .andExpect(status().is(401));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/api/authenticate")
                        .content("{\n" +
                                "  \"username\": \"user1\",\n" +
                                "  \"password\": \"user1\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("token");
        String refreshToken = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("refreshToken");

        System.out.println("accessToken : " + accessToken + "\nrefreshToken : " + refreshToken);

        mvc.perform(MockMvcRequestBuilders.post("/api/test-redirect")
                .header("Authorization", "Bearer " + accessToken)
                .header("refreshToken", refreshToken))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/user"));
    }

    @Test
    @DisplayName("user api 테스트")
    public void UserAPITest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/user"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("Full authentication is required to access this resource"));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/api/authenticate")
                        .content("{\n" +
                                "  \"username\": \"user1\",\n" +
                                "  \"password\": \"user1\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("token");
        String refreshToken = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("refreshToken");

        mvc.perform(MockMvcRequestBuilders.get("/api/user")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.nickname").value("user1_nickname"))
                .andExpect(jsonPath("$.authorityDtoSet[0].authorityName").value("ROLE_USER"));

        mvc.perform(MockMvcRequestBuilders.get("/api/user")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("refreshToken", refreshToken));
    }

    @Test
    @DisplayName("admin api 테스트")
    public void AdminAPITest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/user/user1"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("Full authentication is required to access this resource"));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/api/authenticate")
                        .content("{\n" +
                                "  \"username\": \"user1\",\n" +
                                "  \"password\": \"user1\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("token");
        String refreshToken = new JSONObject(mvcResult.getResponse().getContentAsString()).getString("refreshToken");

        mvc.perform(MockMvcRequestBuilders.get("/api/user/user1")
                .header("Authorization", "Bearer " + accessToken)
                .header("refreshToken", refreshToken))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.error").value("권한이 없습니다."));



        MvcResult mvcResultAdmin = mvc.perform(MockMvcRequestBuilders.post("/api/authenticate")
                        .content("{\n" +
                                "  \"username\": \"admin\",\n" +
                                "  \"password\": \"admin\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String accessTokenAdmin = new JSONObject(mvcResultAdmin.getResponse().getContentAsString()).getString("token");
        String refreshTokenAdmin = new JSONObject(mvcResultAdmin.getResponse().getContentAsString()).getString("refreshToken");

        mvc.perform(MockMvcRequestBuilders.get("/api/user/user1")
                        .header("Authorization", "Bearer " + accessTokenAdmin)
                        .header("refreshToken", refreshTokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.nickname").value("user1_nickname"))
                .andExpect(jsonPath("$.authorityDtoSet[0].authorityName").value("ROLE_USER"));

        mvc.perform(MockMvcRequestBuilders.get("/api/user/admin")
                        .header("Authorization", "Bearer " + accessTokenAdmin)
                        .header("refreshToken", refreshTokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.nickname").value("admin"))
                .andExpect(jsonPath("$.authorityDtoSet[0].authorityName").value("ROLE_USER"))
                .andExpect(jsonPath("$.authorityDtoSet[1].authorityName").value("ROLE_ADMIN"));
    }

}