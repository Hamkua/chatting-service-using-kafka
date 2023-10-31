package com.hamkua.chattingserviceusingkafka.chatting.repository;

import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import com.hamkua.chattingserviceusingkafka.chatting.service.ChattingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChattingDaoTest {

    Logger log = LoggerFactory.getLogger(ChattingDaoTest.class.getSimpleName());

    @Autowired
    private ChattingDao chattingDao;

    @Test
    void findAllChattingRoomTest() {

    }

    @Test
    void findAllChattingRoomByUserIdTest() {
    }

    @Test
    @DisplayName("CHATTING_ROOM 테이블 삽입 테스트")
    void createChattingRoomTest() {

        //when
        Long chattingRoomId = chattingDao.createChattingRoom();

        //then
//        log.info(chattingRoomId.toString());
        assertNotNull(chattingRoomId);
    }

    @Test
    @DisplayName("CHATTING_ROOM_USER 테이블 삽입 테스트")
    void createChattingRoomUserTest() {

        //given
        Long chattingRoomId = Long.MAX_VALUE;
        Long userId = Long.MAX_VALUE;

        //when
        Map<String, Long> keys = chattingDao.createChattingRoomUser(chattingRoomId, userId);

        //then
        assertEquals(2, keys.size());

    }
    
    @Test
    @DisplayName("CHATTING_ROOM 테이블에 pk 존재 여부 테스트")
    void existsChattingRoomTest(){
        
        //given
        Long chattingRoomId = Long.MIN_VALUE;
        
        //when
        Boolean doesExist= chattingDao.existsChattingRoom(chattingRoomId);

        //then
        assertFalse(doesExist);
    }

    @Test
    void findAllChattingRoomUserByChattingRoomIdTest(){

        //given
        Long chattingRoomId = Long.MIN_VALUE;

        //when
        List<ChattingRoomUserDto> result = chattingDao.findAllChattingRoomUserByChattingRoomId(chattingRoomId);

        //then
        assertEquals(0, result.size());

    }

    @Test
    void deleteChattingRoomUserTest(){

        //given
        Long chattingRoomId = 3L;
        Long userId = 2L;

        //when
        Integer i = chattingDao.deleteChattingRoomUser(chattingRoomId, userId);

        //then
        assertEquals(1, i);
    }
}