package com.example.jwttest.integrationTest;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional                      // 테스트코드의 Transactional 은 다르게 동작한다. 모르면 검색하고 오자.
public class ControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    JSONObject jsonObject;

    @Test
    public void helloTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> mvcResult.getResponse().getContentAsString().equals("Hello World!"));
    }

    @Test
    public void signupTest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/signup")
                    .content("{\n" +
                                "  \"nickname\": \"test\",\n" +
                                "  \"password\": \"test\",\n" +
                                "  \"username\": \"test\"\n" +
                                "}")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/api/signup")
                .content("{\n" +
                        "  \"nickname\": \"admin\",\n" +
                        "  \"password\": \"admin\",\n" +
                        "  \"username\": \"admin\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

}