package com.hamkua.chattingserviceusingkafka.chatting;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class ConsumerManagerTest {

    @Autowired
    private ConsumerManager consumerManager;

    @Test
    @Order(1)
    void addConsumerWorkerTest() {

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
}