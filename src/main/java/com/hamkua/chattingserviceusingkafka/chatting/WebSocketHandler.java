package com.hamkua.chattingserviceusingkafka.chatting;

import com.hamkua.chattingserviceusingkafka.chatting.service.ChattingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    Logger log = LoggerFactory.getLogger(WebSocketHandler.class.getSimpleName());

    private final ChattingService chattingService;
    private final ConsumerManager consumerManager;

    // 이후 일급컬렉션으로 변경하고 관련 로직을 이동시키자.
    private Map<Long, Map<Long, WebSocketSession>> chattingRooms = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        Long chattingRoomId = (Long) session.getAttributes().get("chattingRoomId");
//        Long userId = (Long) session.getAttributes().get("userId");

        Map<Long, WebSocketSession> chattingRoom = chattingRooms.get(chattingRoomId);
        Set<Long> userIds = chattingRoom.keySet();

        for(Long userId : userIds){
            WebSocketSession webSocketSession = chattingRoom.get(userId);
            try {
                webSocketSession.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long chattingRoomId = (Long) session.getAttributes().get("chattingRoomId");
        Long userId = (Long) session.getAttributes().get("userId");

        //컨슈머 워커 생성
        Boolean isWorkerAdded = consumerManager.addConsumerWorker(chattingRoomId, userId);
        if(!isWorkerAdded){
            throw new RuntimeException("워커 생성 실패");
        }


        Map<Long, WebSocketSession> chattingRoom;

        boolean doesRoomExist = chattingRooms.containsKey(chattingRoomId);
        if(!doesRoomExist){

            log.info("채팅방 map이 없으므로 생성");
            chattingRoom = new HashMap<>();

        }else {
            log.info("채팅방이 존재함");
            chattingRoom = chattingRooms.get(chattingRoomId);

        }
        chattingRoom.put(userId, session);
        chattingRooms.put(chattingRoomId, chattingRoom);

    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Long chattingRoomId = (Long) session.getAttributes().get("chattingRoomId");
        Long userId = (Long) session.getAttributes().get("userId");

        // 컨슈머 워커 삭제
        Boolean isWorkerDeleted = consumerManager.subConsumerWorker(chattingRoomId, userId);
        if(!isWorkerDeleted){
            throw new RuntimeException("워커 삭제 실패");
        }

        Map<Long, WebSocketSession> chattingRoom = this.chattingRooms.get(chattingRoomId);
        chattingRoom.remove(userId);


        if(chattingRoom.size() == 0){
            chattingRooms.remove(chattingRoomId);
            log.info("채팅방의 유저가 비어있음");
        }else{
            chattingRooms.put(chattingRoomId, chattingRoom);
            log.info("채팅방의 유저가 남아있음");
        }
    }




}
