package com.teamseven.MusicVillain.Security.OAuth;
import java.util.*;
public abstract class OAuth2UserInfo {
    // provider 마다 attribute 가 다르기 때문에, 각각의 provider 에 맞는 attribute는 각각의 class 에서 구현한다.
protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {

            this.attributes = attributes;
            }

    public Map<String, Object> getAttributes() {
            return attributes;
            }

    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getImageUrl();
    public abstract String getFirstName();
    public abstract String getLastName();

}