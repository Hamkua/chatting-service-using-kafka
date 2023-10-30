package com.hamkua.chattingserviceusingkafka.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDto {
    private Long id;
    private String accessToken;
    private String refreshToken;
}
