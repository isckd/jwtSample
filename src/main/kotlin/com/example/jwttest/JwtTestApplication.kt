package com.example.jwttest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class JwtTestApplication
fun main(args: Array<String>) {
    runApplication<JwtTestApplication>(*args)
}
