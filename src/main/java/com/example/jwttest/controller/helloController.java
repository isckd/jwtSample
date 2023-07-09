package com.example.jwttest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class helloController {

    @GetMapping("/hello")
    public ResponseEntity<String> returnHello() {
        return ResponseEntity.ok("Hello World!");
    }
}
