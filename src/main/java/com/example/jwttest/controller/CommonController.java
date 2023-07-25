package com.example.jwttest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @PostMapping("/tokenTest")
    public ResponseEntity<String> validateAccessToken() {
        // TODO filter 에서 검증

        log.info("token is valid");
        return ResponseEntity.ok("Token is valid");
    }
}
