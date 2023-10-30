package com.hamkua.chattingserviceusingkafka.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserVo {
    private Long userId;
    private Long profileImageId;
    private String email;
    private String username;
    private String password;
    private Integer state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
