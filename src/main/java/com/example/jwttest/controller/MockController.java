package com.example.jwttest.controller;

import com.example.jwttest.entity.RefreshToken;
import com.example.jwttest.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.example.jwttest.jwt.JwtFilter.address;

@RestController
@RequestMapping("/api/mock")
public class MockController {

    private static final Logger log = LoggerFactory.getLogger(MockController.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public MockController(RefreshTokenRepository refreshTokenRepository, RestTemplate restTemplate) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 웹/앱에 있다는 가정 하에 만든 mock Controller
     * 여기서는 DB 에서 조회해서 가져오지만,
     * 실제로는 웹/앱 단에서 사용자의 refreshToken 을 가져와야 한다.
     */
    @GetMapping("/getRefresh")
    public ResponseEntity<String> getRefresh(@RequestParam String originURI, @RequestParam String userId) throws IOException {
        log.info("웹/앱 에서 받는다는 가정하에 만든 mock Controller");
        RefreshToken refreshTokenObj = refreshTokenRepository.findById(userId).orElseThrow(() -> new RuntimeException("refreshToken 이 없습니다."));

        HttpEntity<String> request = new HttpEntity<>(refreshTokenObj.getRefreshToken());
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(address + "/api/v1/refresh?originURI=" + originURI, request, String.class);

    }
}
