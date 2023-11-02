package com.hamkua.chattingserviceusingkafka.user;

import com.hamkua.chattingserviceusingkafka.user.service.UserFacadeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilter {

    Logger log = LoggerFactory.getLogger(JwtAuthFilter.class.getSimpleName());

    private final UserFacadeService userFacadeService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(((HttpServletRequest)request).getHeader("Authorization") == null){
            chain.doFilter(request, response);
            return;
        }


        boolean isBearer = ((HttpServletRequest) request).getHeader("Authorization").startsWith("Bearer ");

        if(!isBearer){
            chain.doFilter(request, response);
            return;
        }

        String token = ((HttpServletRequest)request).getHeader("Authorization").substring("Bearer ".length());
        log.info("토큰 : " + token);

        if(token != null){

            Boolean isAuthenticated = userFacadeService.authenticate(token);

            if(!isAuthenticated){
                log.info("인증 실패 - 로그인");
                chain.doFilter(request, response);
            }

        }
    }
}
