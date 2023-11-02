package com.hamkua.chattingserviceusingkafka.chatting;

import com.hamkua.chattingserviceusingkafka.user.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    Logger log = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class.getSimpleName());

    private final UserFacadeService userFacadeService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if(!request.getHeaders().containsKey("Authorization")){
            log.info("웹 소켓 인증 실패");
            return false;
        }
        log.info("{}", request.getHeaders().get("Authorization").get(0));


        String authorization = request.getHeaders().get("Authorization").get(0);

        boolean isBearer = authorization.startsWith("Bearer ");

        if(!isBearer){
            log.info("웹 소켓 인증 실패");
            return false;
        }

        String token = authorization.substring("Bearer ".length());
        log.info("토큰 : " + token);


        Boolean isAuthenticated = userFacadeService.authenticate(token);

        if(!isAuthenticated){
            log.info("웹 소켓 인증 실패");
            return false;
        }

        return true;
    }
}
