package com.hamkua.chattingserviceusingkafka.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// user는 loadUserByUsername이 리턴하는 타입에서 get + user 라는 메서드가 존재해야 하는 것 같다. getter 없으면 Exception 뜸.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : user") //만약 현재 참조중인 객체가 AnonymousAuthenticaionFilter에 의해 생성된 Authentication인 경우 null을 반환하고, 아니라면 loadUser어쩌구 리턴타입인 객체로 간주하고 username 반환한다?
public @interface CurrentUser {
}
