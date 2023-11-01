package com.hamkua.chattingserviceusingkafka.chatting.service;

import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class ChattingServiceTest {

    @Autowired
    private ChattingService chattingService;

    @Test
    @Transactional
    void joinChattingRoomTest() {

        //given
        // 아무 숫자나 넣어도 됨
        Long chattingRoomId = 3L;
        Long userId = 1L;
        ChattingRoomUserDto chattingRoomUserDto = new ChattingRoomUserDto(chattingRoomId, userId);

        //when
        Boolean isJoined = chattingService.enterChattingRoom(chattingRoomUserDto);

        //then
        assertTrue(isJoined);
    }

    @Test
    @Transactional
    void createChattingRoomTest(){
        //given
        ArrayList<ChattingRoomUserDto> invitedUsers = new ArrayList<>(Arrays.asList(new ChattingRoomUserDto(Long.MAX_VALUE), new ChattingRoomUserDto(Long.MAX_VALUE - 1)));


        //when
        Boolean isCreated = chattingService.createChattingRoom(invitedUsers);


        //then
        assertTrue(isCreated);
    }
}