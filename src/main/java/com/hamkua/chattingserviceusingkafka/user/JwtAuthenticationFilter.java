package com.hamkua.chattingserviceusingkafka.user;


import com.hamkua.chattingserviceusingkafka.user.exception.RefreshException;
import com.hamkua.chattingserviceusingkafka.user.service.JwtUtils;
import com.hamkua.chattingserviceusingkafka.user.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class.getSimpleName());

    private final UserFacadeService userFacadeService;
    private final JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 토큰 존재 여부 확인
        Boolean doesExist = jwtUtils.existsToken(request);
        if(!doesExist){
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 추출
        String token = ((HttpServletRequest)request).getHeader("Authorization").substring("Bearer ".length());

        log.info("추출한 토큰 : {}", token);
        // 토큰이 유저 테이블에 존재하는가
        Boolean isValid = jwtUtils.isTokenValid(token);

        log.info("토큰 : " + token);

        if(!isValid){
            log.info("유효하지 않은 토큰");
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 만료 시 재발급
        Boolean isExpired = jwtUtils.isTokenExpired(token);
        if(isExpired){

            log.info("재발급!");
            //새로 발급받은 토큰은 컨트롤러에서 ResponseEntity 반환과 함께 돌려줄 예정.
            token = jwtUtils.reIssue(token);
            ((HttpServletResponse)response).setHeader("Authorization", "Bearer " + token);
        }

        log.info("토큰 문제 없음!");
        userFacadeService.authenticate(token);
        filterChain.doFilter(request, response);
    }
}
