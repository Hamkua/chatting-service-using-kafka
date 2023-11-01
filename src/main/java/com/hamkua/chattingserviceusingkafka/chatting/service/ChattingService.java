package com.hamkua.chattingserviceusingkafka.chatting.service;

import com.hamkua.chattingserviceusingkafka.chatting.ConsumerManager;
import com.hamkua.chattingserviceusingkafka.chatting.KafkaService;
import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomDto;
import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import com.hamkua.chattingserviceusingkafka.chatting.repository.ChattingDao;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChattingService {

    Logger log = LoggerFactory.getLogger(ChattingService.class.getSimpleName());

    private final ChattingDao chattingDao;
    private final KafkaService kafkaService;
    private final ConsumerManager consumerManager;

//    유저가 소속된 채팅방 수 만큼 워커 생성 - 처음
    public void createConsumer(Long userId){
        List<ChattingRoomDto> chattingRooms = chattingDao.findAllChattingRoomByUserId(userId);

        chattingRooms.forEach(
                chattingRoomDto -> consumerManager.addConsumerWorker(chattingRoomDto.getId(), userId)
        );
    }

    public Boolean joinChattingRoom(Long chattingRoomId, Long userId){
        if(!chattingDao.existsChattingRoom(chattingRoomId)){

            log.info("해당 채팅방이 존재하지 않음. 채팅방 번호 : {}", chattingRoomId);
            return false;
        }
        Map<String, Long> compositeKeys = chattingDao.createChattingRoomUser(chattingRoomId, userId);

        Boolean isAdded = consumerManager.addConsumerWorker(chattingRoomId, userId);

        return compositeKeys.size() == 2 && isAdded;
    }

    public Boolean createChattingRoom(List<Long> userIds){
        Long chattingRoomId = chattingDao.createChattingRoom();
        log.info("채팅방 번호 : {}", chattingRoomId);

        boolean isCreated = kafkaService.createTopic("test" + chattingRoomId);
        if(!isCreated){
            throw new RuntimeException("토픽 생성 실패");
        }

        for(Long userId : userIds) {

            Map<String, Long> compositeKeys = chattingDao.createChattingRoomUser(chattingRoomId, userId);
            Boolean isAdded = consumerManager.addConsumerWorker(chattingRoomId, userId);


            if(compositeKeys.size() != 2 || !isAdded){    // 중간에 문제가 생기는 경우 원래 상태로 되돌린다.
                List<ChattingRoomUserDto> chattingRoomUsers = chattingDao.findAllChattingRoomUserByChattingRoomId(chattingRoomId);

                for(ChattingRoomUserDto chattingRoomUser : chattingRoomUsers) {

                    consumerManager.subConsumerWorker(chattingRoomId, chattingRoomUser.getUserId());
                }

                chattingDao.deleteChattingRoomUser(chattingRoomId);
                return false;
            }
        }

        return true;
    }
}
