package com.teamseven.MusicVillain.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuthController {

    // OAuth2 로그인 성공시 토큰 생성해서 보내주는 부분
    @GetMapping("/OAuth/loginSuccess")
    @ResponseBody // 추후 API 구현되면 제거
    public String login(){
        return "login";
    }

    @GetMapping("/OAuth/loginFailure")
    @ResponseBody // 추후 API 구현되면 제거
    public String loginFailure(){
        return "loginFailure";
    }

}
