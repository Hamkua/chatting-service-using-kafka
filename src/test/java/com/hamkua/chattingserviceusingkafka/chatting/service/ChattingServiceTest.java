package com.hamkua.chattingserviceusingkafka.chatting.service;

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


        //when
        Boolean isJoined = chattingService.joinChattingRoom(chattingRoomId, userId);

        //then
        assertTrue(isJoined);
    }

    @Test
    @Transactional
    void createChattingRoomTest(){
        //given
        List<Long> userIds = new ArrayList<>(Arrays.asList(Long.MAX_VALUE, Long.MAX_VALUE - 1));


        //when
        Boolean isCreated = chattingService.createChattingRoom(userIds);


        //then
        assertTrue(isCreated);
    }
}