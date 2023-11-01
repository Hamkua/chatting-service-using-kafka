package com.hamkua.chattingserviceusingkafka.chatting;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    Logger log = LoggerFactory.getLogger(WebSocketHandler.class.getSimpleName());
    private Map<Long, Map<Long, WebSocketSession>> chattingRooms = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        URI uri = session.getUri();
        String path = uri.getPath();

        log.info(path);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long requestedChattingRoomId = getRequestedChattingRoomId(session);


    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }


    Long getRequestedChattingRoomId(WebSocketSession session){
        URI uri = session.getUri();
        String path = uri.getPath();

        String[] strings = path.split("/");

        return Long.valueOf(strings[strings.length - 1]);
    }
}
