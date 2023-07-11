package com.example.jwttest.dto;


import lombok.*;

import java.util.Optional;

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

    private String refreshToken;

}
