package com.hamkua.chattingserviceusingkafka.user.service;


import com.hamkua.chattingserviceusingkafka.user.dto.UserLoginRequestDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserRegisterRequestDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacadeService {

    Logger log = LoggerFactory.getLogger(UserFacadeService.class.getSimpleName());

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailService customUserDetailService;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void registerUser(UserRegisterRequestDto userRegisterRequestDto){
        userService.registerUser(userRegisterRequestDto);
    }

    @Transactional
    public String login(UserLoginRequestDto userLoginRequestDto){
        UserVo userVo = userService.getUserByEmail(userLoginRequestDto.getEmail());

        if(!passwordEncoder.matches(userLoginRequestDto.getPassword(), userVo.getPassword())){
            throw new RuntimeException("비밀번호 틀림");
        }

        return jwtUtils.createToken(userVo);
    }




    @Transactional
    public void authenticate(String token) {


        String username = jwtUtils.extractUsernameFromToken(token);

        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);    // 다운캐스팅
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

//    public Authentication getAuthentication(String token){
//
//        String username = jwtUtils.extractUsernameFromToken(token);
//        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);    // 다운캐스팅
//
//        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
//    }
}
