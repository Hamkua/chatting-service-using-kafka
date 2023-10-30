package com.hamkua.chattingserviceusingkafka.user.service;

import com.hamkua.chattingserviceusingkafka.user.dto.UserTokenDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import com.hamkua.chattingserviceusingkafka.user.repository.UserDao;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtUtils {

    Logger log = LoggerFactory.getLogger(JwtUtils.class.getSimpleName());

//    private final CustomUserDetailService customUserDetailService;
    private final UserDao userDao;

    private long accessValidTime = Duration.ofMinutes(30).toMillis();
    private long refreshValidTime = Duration.ofDays(7).toMillis();

    @Value("${jwt.signing.key}")
    private String key;


    public String createToken(String username, long validTime){
        Date now = new Date();

        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        log.info("token : " + token);
        return token;
    }

    public String createToken(UserVo userVo){

        Long userId = userVo.getUserId();
        String accessToken = createToken(userVo.getUsername(), accessValidTime);
        String refreshToken = createToken(userVo.getUsername(), refreshValidTime);

        UserTokenDto userTokenDto = new UserTokenDto(userId, accessToken, refreshToken);

        if(userDao.isExistUserToken(userId)) {
            userDao.updateUserToken(userTokenDto);

        }else{
            userDao.createUserToken(userTokenDto);
        }

        return accessToken;
    }


    public String reIssue(String accessToken) {
            UserVo userVo = userDao.findUserByAccessToken(accessToken);

            String token = createToken(userVo);

            return token;
    }

    // Bearer 이 없을 경우 처리 로직 추가 필요
    public String extractTokenFromRequest(ServletRequest request){
        String token = ((HttpServletRequest)request).getHeader("Authorization").substring("Bearer ".length());

        return token;
    }

    public Claims extractClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsernameFromToken(String token){
        String username = this.extractClaimsFromToken(token).getSubject();

        return username;
    }

    public Boolean isTokenExist(String token){
        try {
            userDao.findUserByAccessToken(token);

            return true;
        }catch (Exception e){

            log.info("잘못된 토큰");
            return false;
        }
    }


    public Boolean isTokenExpired(String token){
//        return !this.extractClaimsFromToken(token).getExpiration().before(new Date());
//         parseClaimsJws() 메서드를 호출할 때 만료일이 지나면 ExpiredJwtException 예외 발생
        try{
            Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);

            return false;
        }catch(ExpiredJwtException e){
            log.info("토큰 만료");
            return true;
        }
    }

//    public Authentication getAuthentication(String token){
//
//        String username = this.extractUsernameFromToken(token);
//        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);    // 다운캐스팅
//
//        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
//    }

    public Boolean isTokenValid(String token){
        Boolean isTokenExist = this.isTokenExist(token);

        if(isTokenExist) {    // 토큰이 사용된 적 있는 경우
            if (this.isTokenExpired(token)) {    // 토큰이 만료된 경우
                log.info("토큰 만료");

                token = this.reIssue(token);
            }
//            Authentication authentication = this.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);

            return true;
        }
        return false;
    }
}
