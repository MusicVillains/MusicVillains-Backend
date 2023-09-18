package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Utils.ENV;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestController
@Slf4j
public class OAuthController {

    private OAuthService oAuthService;
    @Autowired
    public OAuthController(OAuthService oAuthService){
        this.oAuthService = oAuthService;
    }

    /* DEBUG:
         - description: for test
         - requriements:
            * need to change kakao redirect uri property(in `applicaiton.yml`) to "/oauth2/kakao/callback"
            * need to register Redirect URI for "/oauth2/kakao/callback to Kakao's developers(myApp > kakao login > redirect URI)
            * after test, need to change Kakao's redirect URI in com.teamseven.MusicVillain.Security.OAuth.OAuthService
         - see:
            * kakao developer's setting: https://developers.kakao.com*/
    @GetMapping("/oauth2/kakao/callback")
    public Object catchKakaoAuthenticationCodeCallBack(@RequestParam("code") String code){
        log.trace("> Enter catchKakaoAuthenticationCodeCallBack()");
        log.trace("* code: {}", code);

        // return code;
        KakaoOAuthLoginRequestBody body = new KakaoOAuthLoginRequestBody();
        body.code = code;

        ServiceResult kakaoOauthLoginResult = oAuthService.kakaoOauthLogin_TestOnly(body.code);

        if(kakaoOauthLoginResult.isFailed()) return ResponseObject.onlyData(
                Status.AUTHENTICATION_FAIL,
                kakaoOauthLoginResult.getMessage());

        HashMap serviceResultData = (HashMap)kakaoOauthLoginResult.getData();

        LoginSuccessResponseBody loginSuccessResponseBody = new LoginSuccessResponseBody();
        loginSuccessResponseBody.memberId = serviceResultData.get("memberId").toString();
        loginSuccessResponseBody.tokenType = serviceResultData.get("tokenType").toString();
        loginSuccessResponseBody.accessToken = serviceResultData.get("accessToken").toString();
        loginSuccessResponseBody.refreshToken = serviceResultData.get("refreshToken").toString();

        return ResponseObject.of(Status.OK,
                kakaoOauthLoginResult.getMessage(),
                loginSuccessResponseBody);
    }

    /* TODO: just for test */
    @GetMapping("/kakaoLoginTest")
    public RedirectView kakaoLoginPage(){
        RedirectView redirectView = new RedirectView();
        // return code;
        redirectView.setUrl("https://kauth.kakao.com/oauth/authorize?response_type=code&" +
                "client_id=" + ENV.KAKAO_CLIENT_ID +
                "&redirect_uri=http://localhost:8080/oauth2/kakao/callback");
        return redirectView;
    }

    /* TODO: Test needed */
    @PostMapping("/oauth2/kakao/login")
    public ResponseObject kakaoLogin(@RequestBody KakaoOAuthLoginRequestBody kakaoOAuthLoginRequestBody){
        log.trace("> Enter kakaoLogin()\n"+
                "\twith kakaoOAuthLoginRequestBody.code: {} ", kakaoOAuthLoginRequestBody.code);

        if(kakaoOAuthLoginRequestBody.code == null){
            return ResponseObject.of(Status.BAD_REQUEST,
                    "Kakao authorization code is null", null);
        }

        ServiceResult kakaoOauthLoginResult = oAuthService.kakaoOauthLogin(kakaoOAuthLoginRequestBody.code,
                kakaoOAuthLoginRequestBody.redirectUri);

        // if failed to login
        if(kakaoOauthLoginResult.isFailed()) return ResponseObject.onlyData(
                Status.AUTHENTICATION_FAIL,
                Status.AUTHENTICATION_FAIL.getMessage());

        // get ServiceResult's data and casting to Map
        HashMap serviceResultData = (HashMap)kakaoOauthLoginResult.getData();

        LoginSuccessResponseBody loginSuccessResponseBody = new LoginSuccessResponseBody();
        loginSuccessResponseBody.memberId = serviceResultData.get("memberId").toString();
        loginSuccessResponseBody.tokenType = serviceResultData.get("tokenType").toString();
        loginSuccessResponseBody.accessToken = serviceResultData.get("accessToken").toString();
        loginSuccessResponseBody.refreshToken = serviceResultData.get("refreshToken").toString();

        /* Not use header

        // initialize HttpHeaders
        HttpHeaders headers = new HttpHeaders();

        // put access token to header(Authorization)
        headers.add("Authorization",
                "Bearer " + serviceResultData.get("accessToken").toString());

        // put access token to header(Refresh)
        headers.add("Refresh",
                serviceResultData.get("refreshToken").toString());

        // Response Rawdata = dto of logged-in member including accessToken and refreshToken in header
        return new ResponseObject(headers, Status.OK,
                kakaoOauthLoginResult.getMessage(),
                serviceResultData.get("member"));
        */

        return ResponseObject.of(Status.OK,
                 kakaoOauthLoginResult.getMessage(),
                loginSuccessResponseBody);
    }

    @PostMapping("/oauth2/kakao/logout")
    public ResponseObject kakaoLogout(@RequestHeader HttpHeaders requestHeader){
        String authorization = JwtManager.getAuthorizationFieldFromHttpHeaders(requestHeader);

        log.trace(authorization);
        ServiceResult kakaoLogoutServiceResult =
                oAuthService.kakaoOauthLogout(authorization);
        log.trace("> Enter kakaoLogout()\n"+
                "\twith accessToken: {} ", authorization);
        log.trace("* kakaoLogoutServiceResult Message: {}", kakaoLogoutServiceResult.getMessage());

        if(kakaoLogoutServiceResult.isFailed()) return ResponseObject.onlyData(
                Status.AUTHENTICATION_FAIL,
                kakaoLogoutServiceResult.getMessage());

        return ResponseObject.onlyData(Status.OK,
                kakaoLogoutServiceResult.getMessage());
    }

    @PostMapping("/oauth2/kakao/unlink")
    public ResponseObject kakaoUnlink(@RequestHeader HttpHeaders requestHeader) {
        log.trace("> Enter kakaoUnlink()");
        String authorization = JwtManager.getAuthorizationFieldFromHttpHeaders(requestHeader);
        ServiceResult kakaoUnlinkServiceResult =
                oAuthService.unlinkMember(authorization);

        if(kakaoUnlinkServiceResult.isFailed())
            return ResponseObject.onlyData(Status.AUTHENTICATION_FAIL,
                    kakaoUnlinkServiceResult.getMessage());

        return ResponseObject.onlyData(Status.OK, kakaoUnlinkServiceResult.getMessage());

    }
    @PostMapping("/token/refresh")
    public ResponseObject kakaoUnlink(@RequestBody AccessTokenRefreshRequestBody accessTokenRefreshRequestBody) {
        String refreshToken = accessTokenRefreshRequestBody.refreshToken;
        ServiceResult result = oAuthService.refreshAccessToken(refreshToken);
        if(result.isFailed())
            return ResponseObject.onlyData(Status.UNAUTHORIZED,
                    "Refresh failed");

        String refreshedAccessToken = result.getData().toString();
        Map responseDataMap = Map.of("accessToken", refreshedAccessToken);
        return ResponseObject.onlyData(Status.OK, responseDataMap);
    }


}
