package com.hamkua.chattingserviceusingkafka.chatting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class KafkaServiceTest {

    @Autowired
    private KafkaService kafkaService;

    @Test
    @DisplayName("토픽 생성 테스트")
    void createTopicTest() {

        //given
        String topicName = "test00";

        //when
        boolean isTopicCreated = kafkaService.createTopic(topicName);

        //then
        assertTrue(isTopicCreated);
    }
}