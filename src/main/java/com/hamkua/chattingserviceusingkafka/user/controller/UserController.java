package com.hamkua.chattingserviceusingkafka.user.controller;

import com.hamkua.chattingserviceusingkafka.user.CurrentUser;
import com.hamkua.chattingserviceusingkafka.user.dto.UserLoginRequestDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserRegisterRequestDto;
import com.hamkua.chattingserviceusingkafka.user.dto.UserVo;
import com.hamkua.chattingserviceusingkafka.user.service.UserFacadeService;
import com.hamkua.chattingserviceusingkafka.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    Logger log = LoggerFactory.getLogger(UserController.class.getSimpleName());

    private final UserService userService;

    private final UserFacadeService userFacadeService;





    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Object> registerUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto){
        userFacadeService.registerUser(userRegisterRequestDto);

        return ResponseEntity.ok("registered.");
    }

//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute("userRegisterRequestDto") UserRegisterRequestDto userRegisterRequestDto){
//
//        log.info(userRegisterRequestDto.toString());
//        userFacadeService.registerUser(userRegisterRequestDto);
//
//        return "index";
//    }

//    @GetMapping("/register")
//    public String registerPage(Model model, UserRegisterRequestDto userRegisterRequestDto){
//        model.addAttribute("userRegisterRequestDto");
//
//        return "authentication-register";
//    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Object> login(@RequestBody UserLoginRequestDto userLoginRequestDto){
        String token = userFacadeService.login(userLoginRequestDto);

        return ResponseEntity.ok(token);
    }

//    @PostMapping("/login")
//    public String login(@ModelAttribute("userLoginRequestDto") UserLoginRequestDto userLoginRequestDto){
//        String token = userFacadeService.login(userLoginRequestDto);
//
//        return "index";
//    }


//    @GetMapping
//    @ResponseBody
//    public ResponseEntity<Object> index(@CurrentUser UserVo user){
//        return ResponseEntity.ok(user.toString());
//    }

    @GetMapping
    public String index(@CurrentUser UserVo user){
        return "index";
    }


//    @GetMapping("/login")
//    public String loginPage(Model model, UserLoginRequestDto userLoginRequestDto){
//
//        model.addAttribute("userLoginRequestDto", userLoginRequestDto);
//        return "authentication-login";
//    }
}
