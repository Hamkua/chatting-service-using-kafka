package com.hamkua.chattingserviceusingkafka.chatting.service;

import com.hamkua.chattingserviceusingkafka.chatting.ConsumerManager;
import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomDto;
import com.hamkua.chattingserviceusingkafka.chatting.repository.ChattingDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final ChattingDao chattingDao;
    private final ConsumerManager consumerManager;

//    유저가 소속된 채팅방 수 만큼 워커 생성
    public void createConsumer(Long userId){
        List<ChattingRoomDto> chattingRooms = chattingDao.findAllChattingRoomByUserId(userId);

        chattingRooms.forEach(
                chattingRoomDto -> consumerManager.addConsumerWorker(chattingRoomDto.getId(), userId)
        );
    }

}
