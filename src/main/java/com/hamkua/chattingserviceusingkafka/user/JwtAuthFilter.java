package com.hamkua.chattingserviceusingkafka.user;

import com.hamkua.chattingserviceusingkafka.user.service.JwtUtils;
import com.hamkua.chattingserviceusingkafka.user.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilter {

    Logger log = LoggerFactory.getLogger(JwtAuthFilter.class.getSimpleName());

    private final UserFacadeService userFacadeService;
    private final JwtUtils jwtUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        // 토큰 존재 여부 확인
        Boolean doesExist = jwtUtils.existsToken(request);
        if(!doesExist){
            chain.doFilter(request, response);
            return;
        }

        // 토큰 추출
        String token = ((HttpServletRequest)request).getHeader("Authorization").substring("Bearer ".length());

        // 토큰이 유저 테이블에 존재하는가
        Boolean isValid = jwtUtils.isTokenValid(token);

        log.info("토큰 : " + token);

        if(!isValid){
            chain.doFilter(request, response);
            return;
        }

        //토큰 만료 시 재발급
        Boolean isExpired = jwtUtils.isTokenExpired(token);
        if(isExpired){
            token = jwtUtils.reIssue(token);
            ((HttpServletResponse)response).setHeader("Authorization", "Bearer " + token);
        }

        userFacadeService.authenticate(token);
    }
}
