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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    Logger log = LoggerFactory.getLogger(WebSocketHandler.class.getSimpleName());

    private final ChattingService chattingService;
    private final ConsumerManager consumerManager;
    private Map<Long, Map<Long, WebSocketSession>> chattingRooms = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        URI uri = session.getUri();
        String path = uri.getPath();

        log.info(path);

        HttpHeaders handshakeHeaders = session.getHandshakeHeaders();
        List<String> authorization = handshakeHeaders.get("Authorization");

        log.info("{}", authorization);

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
    }

}
