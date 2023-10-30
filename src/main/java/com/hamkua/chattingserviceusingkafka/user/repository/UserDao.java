package com.hamkua.chattingserviceusingkafka.user.repository;


import com.hamkua.chattingserviceusingkafka.user.dto.UserRegisterRequestDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserTokenDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
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

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    class UserRowMapper implements RowMapper<UserVo> {

        @Override
        public UserVo mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserVo userVo = new UserVo(rs.getLong("id"),
                    rs.getLong("profile_image_id"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("state"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());

            return userVo;
        }
    }

    class UserTokenRowMapper implements RowMapper<UserTokenDto> {

        @Override
        public UserTokenDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserTokenDto userRefreshTokenDto = new UserTokenDto(
                    rs.getLong("id"),
                    rs.getString("access_token"),
                    rs.getString("refresh_token")
            );

            return userRefreshTokenDto;
        }
    }


    public UserVo findUserById(Long userId) {
        String query = "SELECT * FROM USER WHERE ID = ?";

        return this.jdbcTemplate.queryForObject(query, new UserRowMapper(), userId);
    }

    public UserVo findUserByEmail(String email) {
        String query = "SELECT * FROM USER WHERE EMAIL = ?";

        return this.jdbcTemplate.queryForObject(query, new UserRowMapper(), email);
    }


    public Boolean checkEmailExists(String email) {
        String query = "SELECT IF(EXISTS(SELECT * FROM USER WHERE EMAIL = ?), 1, 0)";

        return this.jdbcTemplate.queryForObject(query, Boolean.class, email);
    }


    public Boolean checkUsernameExists(String username) {
        String query = "SELECT IF(EXISTS(SELECT * FROM USER WHERE USERNAME = ?), 1, 0)";
        return this.jdbcTemplate.queryForObject(query, Boolean.class, username);
    }


    public UserVo findUserByUsername(String username) {
        String query = "SELECT * FROM USER WHERE USERNAME = ?";

        return this.jdbcTemplate.queryForObject(query, new UserRowMapper(), username);
    }


    public Long createUser(UserRegisterRequestDto userRegisterRequestDto) {

        String query = "INSERT INTO USER(EMAIL, USERNAME, PASSWORD) VALUES(?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
                        pstmt.setString(1, userRegisterRequestDto.getEmail());
                        pstmt.setString(2, userRegisterRequestDto.getUsername());
                        pstmt.setString(3, userRegisterRequestDto.getPassword());

                        return pstmt;
                    }
                }, keyHolder);

        Number key = keyHolder.getKey();
        return key.longValue();
    }


    public void createUserToken(UserTokenDto userTokenDto) {
        String query = "INSERT INTO USER_TOKEN(ID, ACCESS_TOKEN, REFRESH_TOKEN) VALUES(?, ?, ?)";

        this.jdbcTemplate.update(
                query, userTokenDto.getId(), userTokenDto.getAccessToken(), userTokenDto.getRefreshToken()
        );
    }

    public void updateUserToken(UserTokenDto userTokenDto) {
        String query = "UPDATE USER_TOKEN SET ACCESS_TOKEN = ?, REFRESH_TOKEN = ? WHERE ID = ?";

        this.jdbcTemplate.update(
                query, userTokenDto.getAccessToken(), userTokenDto.getRefreshToken(), userTokenDto.getId()
        );
    }

    public Boolean isExistUserToken(Long userId) {
        String query = "SELECT IF(EXISTS(SELECT * FROM USER_TOKEN WHERE ID = ?), 1, 0)";

        return this.jdbcTemplate.queryForObject(query, Boolean.class, userId);
    }

//    public Boolean isExistUserTokenByAccessToken(String accessToken){
//
//    }

    public UserVo findUserByAccessToken(String accessToken) {
        String query = "select * from USER where USER.id in (select id from USER_TOKEN where access_token = ?)";

        return this.jdbcTemplate.queryForObject(query, new UserRowMapper(), accessToken);
    }
}
