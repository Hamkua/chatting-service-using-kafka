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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChattingService {

    Logger log = LoggerFactory.getLogger(ChattingService.class.getSimpleName());

    private final ChattingDao chattingDao;
    private final KafkaService kafkaService;
    private final ConsumerManager consumerManager;

    public Boolean enterChattingRoom(ChattingRoomUserDto chattingRoomUserDto){

        Long chattingRoomId = chattingRoomUserDto.getChattingRoomId();
        Long userId = chattingRoomUserDto.getUserId();

        if(!chattingDao.existsChattingRoom(chattingRoomId)){

            log.info("해당 채팅방이 존재하지 않음. 채팅방 번호 : {}", chattingRoomId);
            return false;
        }
        Map<String, Long> compositeKeys = chattingDao.createChattingRoomUser(chattingRoomId, userId);

        Boolean isAdded = consumerManager.addConsumerWorker(chattingRoomId, userId);

        return compositeKeys.size() == 2 && isAdded;
    }

    public Boolean createChattingRoom(List<ChattingRoomUserDto> invitedUsers){
        Long chattingRoomId = chattingDao.createChattingRoom();
        log.info("채팅방 번호 : {}", chattingRoomId);

        boolean isCreated = kafkaService.createTopic("test" + chattingRoomId);
        if(!isCreated){
            throw new RuntimeException("토픽 생성 실패");
        }

        for(ChattingRoomUserDto invitedUser : invitedUsers) {
            Long userId = invitedUser.getUserId();

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

    public void exitChattingRoom(ChattingRoomUserDto chattingRoomUserDto){

        Long chattingRoomId = chattingRoomUserDto.getChattingRoomId();
        Long userId = chattingRoomUserDto.getUserId();

        log.info("chattingRoomId : {}, userId : {}", chattingRoomId, userId);

        chattingDao.deleteChattingRoomUser(chattingRoomId, userId);

        Boolean isWorkerDeleted = consumerManager.subConsumerWorker(chattingRoomId, userId);
        if(!isWorkerDeleted){
            throw new RuntimeException("워커 삭제 실패");
        }

        Boolean doesExist = chattingDao.existsChattingRoomUser(chattingRoomId);
        if(!doesExist){
            Boolean isChattingRoomDeleted = chattingDao.deleteChattingRoom(chattingRoomId) == 1;
            Boolean isTopicDeleted = kafkaService.deleteTopic("test" + chattingRoomId);
            if(!isTopicDeleted || !isChattingRoomDeleted){
                throw new RuntimeException("토픽 삭제 실패");
            }
        }
    }
}
