package com.hamkua.chattingserviceusingkafka.chatting;

import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class ConsumerManager {

    Logger log = LoggerFactory.getLogger(ConsumerManager.class.getSimpleName());

    private Properties props;

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    private static List<ConsumerWorker> workers = new ArrayList<>();

    private final Thread mainThread = Thread.currentThread();


    @PostConstruct
    private void init(){
        props = new Properties();

        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("sasl.mechanism", "PLAIN");
        props.setProperty("key.deserializer", StringDeserializer.class.getName());
        props.setProperty("value.deserializer", StringDeserializer.class.getName());
        props.setProperty("auto.offset.reset", "earliest");
    }

    public Boolean addConsumerWorker(Long chattingRoomId, Long userId){

        try {
            ConsumerWorker worker = new ConsumerWorker(props, chattingRoomId, userId);

            workers.add(worker);

            Thread thread = new Thread(worker);
            thread.start();

        }catch(Exception e){
            return false;
        }

        return true;
    }


    public ConsumerWorker getConsumerWorker(Long chattingRoomId, Long userId){
        String threadName = "consumer-thread" + chattingRoomId + userId;

        ConsumerWorker result = null;

        for(ConsumerWorker worker : workers){
            if(threadName.equals(worker.getThreadName())){

                result = worker;
            }
        }

        return result;
    }
}
