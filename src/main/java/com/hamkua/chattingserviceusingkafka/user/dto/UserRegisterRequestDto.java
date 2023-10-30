package com.hamkua.chattingserviceusingkafka.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegisterRequestDto {

    private String email;
    private String username;
    private String password;
    private String passwordConfirm;

}
