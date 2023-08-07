package com.teamseven.MusicVillain;
import java.util.Map;
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String providerType, Map<String, Object> attributes) {
        System.out.println("[DEBUG] OAuth2UserInfoFactory.GetOOAuth2UserInfo()");
        switch (providerType) {
            case ProviderType.GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case ProviderType.KAKAO: return new KakaoOAuth2UserInfo(attributes);
        //  case FACEBOOK: return new FacebookOAuth2UserInfo(attributes);
//          case NAVER: return new NaverOAuth2UserInfo(attributes);

            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}