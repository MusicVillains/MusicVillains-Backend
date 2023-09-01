package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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

        return this.kakaoLogin(body);
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

        ServiceResult kakaoOauthLoginResult = oAuthService.kakaoOauthLogin(kakaoOAuthLoginRequestBody.code);

        // if failed to login
        if(kakaoOauthLoginResult.isFailed()) return ResponseObject.onlyData(
                Status.AUTHENTICATION_FAIL,
                Status.AUTHENTICATION_FAIL.getMessage());

        // get ServiceResult's data and casting to Map
        Map serviceResultData = (Map)kakaoOauthLoginResult.getData();

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
                serviceResultData);
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
