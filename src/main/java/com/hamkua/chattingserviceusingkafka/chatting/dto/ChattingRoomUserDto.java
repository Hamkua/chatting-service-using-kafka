package com.hamkua.chattingserviceusingkafka.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomUserDto {

    private Long chattingRoomId;
    private Long userId;

    public ChattingRoomUserDto(Long userId) {
        this.userId = userId;
    }
}
