package com.hamkua.chattingserviceusingkafka.user.service;


import com.hamkua.chattingserviceusingkafka.user.dto.UserRegisterRequestDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import com.hamkua.chattingserviceusingkafka.user.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    Logger log = LoggerFactory.getLogger(UserService.class.getSimpleName());

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserRegisterRequestDto userRegisterRequestDto){

        Boolean isEmailExists = userDao.checkEmailExists(userRegisterRequestDto.getEmail());
        if(isEmailExists){
            throw new RuntimeException("이메일 이미 존재함");
        }

        Boolean isUsernameExists = userDao.checkUsernameExists(userRegisterRequestDto.getUsername());
        if(isUsernameExists){
            throw new RuntimeException("유저네임 이미 존재함");
        }

        if(!userRegisterRequestDto.getPassword().equals(userRegisterRequestDto.getPasswordConfirm())){
            throw new RuntimeException("비밀번호, 비밀번호 확인 불일치");
        }

        userRegisterRequestDto.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));

        userDao.createUser(userRegisterRequestDto);
    }

    public UserVo getUserByEmail(String email){
        return userDao.findUserByEmail(email);
    }
}
