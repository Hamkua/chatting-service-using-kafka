package com.hamkua.chattingserviceusingkafka.chatting.controller;

import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import com.hamkua.chattingserviceusingkafka.chatting.service.ChattingService;
import com.hamkua.chattingserviceusingkafka.user.CurrentUser;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChattingController {

    Logger log = LoggerFactory.getLogger(ChattingService.class.getSimpleName());

    private final ChattingService chattingService;

    @PostMapping
    public ResponseEntity<Object> createChattingRoom(@CurrentUser UserVo userVo, List<ChattingRoomUserDto> invitedUsers){

        invitedUsers.add(new ChattingRoomUserDto(userVo.getUserId()));

        Boolean isCreated = chattingService.createChattingRoom(invitedUsers);

        if(isCreated){
            return ResponseEntity.ok("채팅방 생성 성공");
        }

        return ResponseEntity.ok("채팅방 생성 실패");
    }


    @PostMapping
    public ResponseEntity<Object> enterChattingRoom(@RequestBody ChattingRoomUserDto chattingRoomUserDto){

        Boolean isEntered = chattingService.enterChattingRoom(chattingRoomUserDto);
        if(isEntered){
            return ResponseEntity.ok("채팅방 입장 성공");
        }

        return ResponseEntity.ok("채팅방 입장 실패");
    }

}
