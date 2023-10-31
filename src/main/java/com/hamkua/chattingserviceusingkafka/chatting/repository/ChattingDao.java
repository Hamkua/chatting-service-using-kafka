package com.hamkua.chattingserviceusingkafka.chatting.repository;

import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomDto;
import com.hamkua.chattingserviceusingkafka.chatting.dto.ChattingRoomUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChattingDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ChattingDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    class ChattingRoomRowMapper implements RowMapper<ChattingRoomDto>{

        @Override
        public ChattingRoomDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ChattingRoomDto chattingRoomDto = new ChattingRoomDto(
                    rs.getLong("id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime(),
                    rs.getInt("state") == 1
            );

            return chattingRoomDto;
        }
    }

    class ChattingRoomUserRowMapper implements RowMapper<ChattingRoomUserDto>{

        @Override
        public ChattingRoomUserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ChattingRoomUserDto chattingRoomUserDto = new ChattingRoomUserDto(
                    rs.getLong("id"),
                    rs.getLong("user_id")
            );

            return chattingRoomUserDto;
        }
    }


    public List<ChattingRoomDto> findAllChattingRoom(){
        String query = "select * from CHATTING_ROOM where state = 1";

        return this.jdbcTemplate.query(query, new ChattingRoomRowMapper());
    }


    public List<ChattingRoomDto> findAllChattingRoomByUserId(Long userId){
        String query = "select * from CHATTING_ROOM where id in (select CHATTING_ROOM_USER.id from CHATTING_ROOM_USER where user_id = ?)";

        return this.jdbcTemplate.query(query, new ChattingRoomRowMapper());
    }

    public Long createChattingRoom(){
        String query = "insert into CHATTING_ROOM values()";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(
                new PreparedStatementCreator() {
                     @Override
                     public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                         PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});

                         return pstmt;
                     }
                 }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Map<String, Long> createChattingRoomUser(Long chattingRoomId, Long userId){
        String query = "insert into CHATTING_ROOM_USER(id, user_id) " +
                "select ?, ? from dual where not exists(select * from CHATTING_ROOM_USER where id = ? and user_id = ?)";

        this.jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pstmt = con.prepareStatement(query);
                        pstmt.setLong(1, chattingRoomId);
                        pstmt.setLong(2, userId);
                        pstmt.setLong(3, chattingRoomId);
                        pstmt.setLong(4, userId);

                        return pstmt;
                    }
                }
        );

        Map<String, Long> keys = new HashMap<>();
        keys.put("id", chattingRoomId);
        keys.put("user_id", userId);

        return keys;
    }

}
