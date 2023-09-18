package com.teamseven.MusicVillain.Security.OAuth;

public class KakaoOAuthLoginRequestBody {
    public String code; // Kakao's Authorization Code
    public String redirectUri; // 카카오 코드를 받은 Redirect URI
}
