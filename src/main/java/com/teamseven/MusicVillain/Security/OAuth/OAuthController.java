package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
public class OAuthController {

    private OAuthService oAuthService;
    @Autowired
    public OAuthController(OAuthService oAuthService){
        this.oAuthService = oAuthService;
    }

    /* TODO: Test needed */
    @PostMapping("/oauth2/kakao/login")
    public ResponseObject kakaoLogin(@RequestBody KakaoOAuthLoginRequestBody kakaoOAuthLoginRequestBody){

        if(kakaoOAuthLoginRequestBody.code == null){
            return ResponseObject.of(Status.BAD_REQUEST,
                    "Kakao authorization code is null", null);
        }

        ServiceResult kakaoOauthLoginResult = oAuthService.kakaoOauthLogin(kakaoOAuthLoginRequestBody.code);

        return kakaoOauthLoginResult.isFailed() ? ResponseObject.of(
                Status.AUTHENTICATION_FAIL, kakaoOauthLoginResult.getMessage(), null)
                : ResponseObject.OK(kakaoOauthLoginResult.getData());
    }
}
