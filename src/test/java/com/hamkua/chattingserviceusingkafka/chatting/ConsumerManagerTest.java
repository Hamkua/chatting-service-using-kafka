package com.hamkua.chattingserviceusingkafka.chatting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class ConsumerManagerTest {

    @Autowired
    private ConsumerManager consumerManager;

    Logger log = LoggerFactory.getLogger(ConsumerManagerTest.class.getSimpleName());

    @Test
    @Order(1)
    @DisplayName("워커 생성 테스트")
    void addConsumerWorkerTest() {
        log.info("addConsumerWorkerTest");

        //given
        Long chattingRoomId = 1L;
        Long userId = 1L;

        //when
        Boolean isAdded = consumerManager.addConsumerWorker(chattingRoomId, userId);

        //then
        assertTrue(isAdded);
    }

    @Test
    @Order(2)
    @DisplayName("워커 조회 테스트")
    void getConsumerWorkerTest(){

        //given
        Long chattingRoomId = 1L;
        Long userId = 1L;
        String expectedThreadName = "consumer-thread" + chattingRoomId + userId;

        //when
        ConsumerWorker worker = consumerManager.getConsumerWorker(chattingRoomId, userId);

        //then
        assertEquals(expectedThreadName, worker.getThreadName());
    }

    @Test
    @Order(3)
    @DisplayName("워커 삭제 테스트")
    void subConsumerWorkerTest() {

        //given
        Long chattingRoomId = 1L;
        Long userId = 1L;

        //when
        Boolean isRemoved = consumerManager.subConsumerWorker(chattingRoomId, userId);

        //then
        assertTrue(isRemoved);
    }


}