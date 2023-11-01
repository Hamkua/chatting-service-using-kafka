package com.hamkua.chattingserviceusingkafka.chatting.service;

import com.hamkua.chattingserviceusingkafka.chatting.ConsumerManager;
import com.hamkua.chattingserviceusingkafka.chatting.KafkaService;
import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import com.hamkua.chattingserviceusingkafka.chatting.repository.ChattingDao;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class ChattingServiceTest {

    @Autowired
    private ChattingService chattingService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ConsumerManager consumerManager;

    @Autowired
    private ChattingDao chattingDao;

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

    @Test
    @Transactional
    void exitChattingRoomTest(){
        //given
        Long chattingRoomId = chattingDao.createChattingRoom();

        ChattingRoomUserDto chattingRoomUserDto = new ChattingRoomUserDto(chattingRoomId, Long.MAX_VALUE);

        ArrayList<ChattingRoomUserDto> invitedUsers = new ArrayList<>(List.of(chattingRoomUserDto));



        //when
        boolean isCreated = kafkaService.createTopic("test" + chattingRoomId);
        if(!isCreated){
            throw new RuntimeException("토픽 생성 실패");
        }

        for(ChattingRoomUserDto invitedUser : invitedUsers) {
            Long userId = invitedUser.getUserId();

            Map<String, Long> compositeKeys = chattingDao.createChattingRoomUser(chattingRoomId, userId);
            Boolean isAdded = consumerManager.addConsumerWorker(chattingRoomId, userId);


            if(compositeKeys.size() != 2 || !isAdded){    // 중간에 문제가 생기는 경우 원래 상태로 되돌린다.
                List<ChattingRoomUserDto> chattingRoomUsers = chattingDao.findAllChattingRoomUserByChattingRoomId(chattingRoomId);

                for(ChattingRoomUserDto chattingRoomUser : chattingRoomUsers) {

                    consumerManager.subConsumerWorker(chattingRoomId, chattingRoomUser.getUserId());
                }

                chattingDao.deleteChattingRoomUser(chattingRoomId);
                throw new RuntimeException("워커 생성 중 문제 발생");
            }
        }

        chattingService.exitChattingRoom(chattingRoomUserDto);

        //then
        Boolean doesTopicExist = kafkaService.existsTopic("test" + chattingRoomId);
        Boolean doesWorkerExist = consumerManager.existsConsumerWorker(chattingRoomId);
        Boolean doesChattingRoomExist = chattingDao.existsChattingRoomUser(chattingRoomId);
        Boolean doesChattingRoomUserExist = chattingDao.existsChattingRoomUser(chattingRoomId);

        assertTrue(!doesTopicExist && !doesWorkerExist && !doesChattingRoomExist && !doesChattingRoomUserExist);
    }
}