package com.hamkua.chattingserviceusingkafka.chatting;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@RequiredArgsConstructor
@ToString
public class ConsumerWorker implements Runnable{

    private final Logger log = LoggerFactory.getLogger(ConsumerWorker.class.getSimpleName());

    private Properties prop;
    private String topic;
    private String threadName;
    private KafkaConsumer<String, String> consumer;


    public ConsumerWorker(Properties prop, String topic, int number) {
        this.prop = prop;
        this.topic = topic;
        this.threadName = "consumer-thread" + number;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(Collections.singletonList(topic));

        try{
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for(ConsumerRecord<String, String> record : records){
                    // 로직 추가 해야..
                    log.info("{}", record);
                }
            }
        }catch (WakeupException e){
            log.info(this.threadName + " wakeup");
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            consumer.close();
        }
    }
}
