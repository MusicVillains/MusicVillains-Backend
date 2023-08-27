package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Hidden
@CrossOrigin(origins = "*")
@Controller
public class OAuthController {

    private OAuthService oAuthService;
    @Autowired
    public OAuthController(OAuthService oAuthService){
        this.oAuthService = oAuthService;
    }

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


    // TODO: Test needed
    @PostMapping("/oauth2/kakao/login")
    public ResponseObject kakaoLogin(KakaoOAuthLoginRequestBody kakaoOAuthLoginRequestBody){

        if(kakaoOAuthLoginRequestBody.code == null){
            return ResponseObject.of(Status.BAD_REQUEST, "Authorization code is null", null);
        }

        ServiceResult kakaoOauthLoginResult = oAuthService.kakaoOauthLogin(kakaoOAuthLoginRequestBody.code);

        return kakaoOauthLoginResult.isFailed() ? ResponseObject.of(
                Status.AUTHENTICATION_FAIL, kakaoOauthLoginResult.getMessage(), null)
                : ResponseObject.OK(kakaoOauthLoginResult.getData());
    }
}
