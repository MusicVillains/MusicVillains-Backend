package com.teamseven.MusicVillain.Security.OAuth;

import java.util.Map;
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String providerType, Map<String, Object> attributes) {
        System.out.println("[DEBUG] OAuth2UserInfoFactory.GetOOAuth2UserInfo()");
        switch (providerType) {
            case OAuth2ProviderType.GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case OAuth2ProviderType.KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}