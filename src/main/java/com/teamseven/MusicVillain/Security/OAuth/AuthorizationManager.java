package com.teamseven.MusicVillain.Security.OAuth;


import com.teamseven.MusicVillain.ENV;
import org.springframework.http.HttpHeaders;

public abstract class AuthorizationManager {
    public boolean authorizeMode = ENV.AUTHORIZE_MODE;

    public AuthorizationResult authorize(HttpHeaders headers, String entityId){
        if (authorizeMode == false)
            return AuthorizationResult.success(entityId, null);
       return this.authorize(headers.getFirst("Authorization"), entityId);
    }

    public abstract AuthorizationResult authorize(String jwtToken, String entityId);

    public abstract AuthorizationResult authenticate(String jwtToken, String entityId);

}
