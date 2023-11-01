package com.hamkua.chattingserviceusingkafka.chatting;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaService {

    Logger log = LoggerFactory.getLogger(KafkaService.class.getSimpleName());

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


    public boolean deleteTopic(String topicName){

        try {
            DeleteTopicsResult deleteTopicsResult = admin.deleteTopics(Collections.singleton(topicName));

        } catch (Exception e){
            return false;
        }

        return true;
    }

    public Boolean existsTopic(String topicName){
        ListTopicsResult listTopicsResult = admin.listTopics();
        try {
            Set<String> topicNames = listTopicsResult.names().get();
            Iterator<String> it = topicNames.iterator();
            while(it.hasNext()){
                boolean doesExist = topicName.equals(it.next());
                if(doesExist){
                    return true;
                }
            }

            return false;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


}
