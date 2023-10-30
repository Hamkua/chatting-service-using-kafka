package com.hamkua.chattingserviceusingkafka.user.service;


import com.hamkua.chattingserviceusingkafka.user.dto.AuthUser;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import com.hamkua.chattingserviceusingkafka.user.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        UserVo userVo = userDao.findUserByUsername(username);

        //userVo가 null일때 로직 추가해야 함.

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

        return AuthUser.builder()
                .user(userVo)
                .authorities(roles)
                .build();

    }
}
