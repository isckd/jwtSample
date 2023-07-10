package com.example.jwttest.dto;


import lombok.*;

/**
 * Response 에 사용할 Token Dto
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    private String token;
}
