package com.hamkua.chattingserviceusingkafka.chatting;

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

    private static final Logger log = LoggerFactory.getLogger(ConsumerManager.class.getSimpleName());

    private Properties props;

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    private static List<ConsumerWorker> workers = new ArrayList<>();

    @PostConstruct
    private void init(){
        props = new Properties();

        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("sasl.mechanism", "PLAIN");
        props.setProperty("key.deserializer", StringDeserializer.class.getName());
        props.setProperty("value.deserializer", StringDeserializer.class.getName());
        props.setProperty("auto.offset.reset", "earliest");
        props.setProperty("group.id", "1");
    }

    public void findAllBrokers(){
        workers.forEach(worker -> log.info(worker.toString()));
    }

    public void addConsumerWorker(int num){
        ConsumerWorker worker = new ConsumerWorker(props, "test", num);

        workers.add(worker);

        Thread thread = new Thread(worker);
        thread.start();

        log.info(Thread.currentThread().getName());
    }

}
