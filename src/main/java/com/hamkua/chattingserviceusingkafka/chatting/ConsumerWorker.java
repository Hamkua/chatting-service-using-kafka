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

    Logger log = LoggerFactory.getLogger(ConsumerWorker.class.getSimpleName());

    private Properties prop;
    private String topic;

    private String threadName;
    private KafkaConsumer<String, String> consumer;


    public ConsumerWorker(Properties prop, Long chattingRoomId, Long userId) {
        this.prop = prop;

        // 각 워커마다 다른 group.id를 부여하지 않으면 파티션을 공유하게 된다.
        prop.setProperty("group.id", String.valueOf(chattingRoomId) + userId);

        this.topic = "test" + chattingRoomId;
        this.threadName = "consumer-thread" + chattingRoomId + userId;

        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void run() {
        log.info("컨슈머 워커 실행 : {}", threadName);
        Thread.currentThread().setName(threadName);

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
            log.info(this.threadName + " close");
            consumer.close();
        }
    }

    public void wakeup(){
        consumer.wakeup();
    }

    public String getThreadName() {
        return threadName;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if(obj instanceof ConsumerWorker){
//            return this.threadName.equals(((ConsumerWorker) obj).threadName);
//        }
//        return false;
//    }
}
