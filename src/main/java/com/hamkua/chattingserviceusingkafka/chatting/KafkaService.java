package com.hamkua.chattingserviceusingkafka.chatting;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaService {

    private AdminClient admin;

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    @PostConstruct
    private void init(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        admin = AdminClient.create(props);
    }

    public boolean createTopic(String topicName){
        try {
            admin.createTopics(Collections.singleton(TopicBuilder.name(topicName).partitions(1).replicas(1).build())).all().get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        return true;
    }




}
