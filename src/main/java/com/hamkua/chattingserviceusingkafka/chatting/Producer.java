package com.hamkua.chattingserviceusingkafka.chatting;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    public void createMessage(String topicName, String message){

        this.kafkaTemplate.send(topicName, message);
    }
}
