package com.hamkua.chattingserviceusingkafka.chatting;

import com.hamkua.chattingserviceusingkafka.chatting.repository.ChattingDao;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import com.hamkua.chattingserviceusingkafka.user.repository.UserDao;
import com.hamkua.chattingserviceusingkafka.user.service.JwtUtils;
import com.hamkua.chattingserviceusingkafka.user.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    Logger log = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class.getSimpleName());

    private final UserFacadeService userFacadeService;
    private final JwtUtils jwtUtils;
    private final UserDao userDao;
    private final ChattingDao chattingDao;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        //
        Boolean doesExist = jwtUtils.existsToken(request);
        if(!doesExist){
            return false;
        }

        //토큰 추출
        String token = request.getHeaders().get("Authorization").get(0).substring("Bearer ".length());
        log.info("토큰 : " + token);


        // 토큰이 유저 테이블에 존재하는가
        Boolean isValid = jwtUtils.isTokenValid(token);
        if(!isValid){
            return false;
        }

        // 웹 세션 연결 중 토큰이 만료될 가능성은?
        //토큰 만료 시 재발급
        Boolean isExpired = jwtUtils.isTokenExpired(token);
        if(isExpired){
            token = jwtUtils.reIssue(token);
            attributes.put("Authorization", "Bearer " + token);
        }

        //인증
        userFacadeService.authenticate(token);

        //@CurrentUser 사용할 수 없으니 userId 추출
        String username = jwtUtils.extractUsernameFromToken(token);
        UserVo userVo = userDao.findUserByUsername(username);
        log.info("{}", userVo);

        Long userId = userVo.getUserId();
        attributes.put("userId", userId);

        // 헤더에 채팅방Id가 존재해야 함.
        if(!request.getHeaders().containsKey("chattingRoomId")){
            return false;
        }

        // 이 유저가 해당 채팅방에 입장할 수 있는지 파악
        Long chattingRoomId = Long.valueOf(request.getHeaders().get("chattingRoomId").get(0));

        Boolean doesChattingRoomIdExist = chattingDao.existsChattingRoom(chattingRoomId);
        Boolean doesChattingRoomUserExist = chattingDao.existsChattingRoomUser(chattingRoomId, userId);

        if(!doesChattingRoomIdExist || !doesChattingRoomUserExist){
            log.info("이 유저는 해당 채팅방에 입장할 수 없어요. chattingRoomId : {} , userId : {}", chattingRoomId, userId);
            return false;
        }

        attributes.put("chattingRoomId", chattingRoomId);
        return true;

    }
}
