package com.teamseven.MusicVillain.Security.JWT;


import com.teamseven.MusicVillain.ENV;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import org.springframework.http.HttpHeaders;

public abstract class JwtAuthorizationManager {
    public boolean authorizeMode = ENV.AUTHORIZE_MODE;

    public AuthorizationResult authorize(HttpHeaders headers, String entityId){
        if (authorizeMode == false)
            return AuthorizationResult.success(entityId, null);
       return this.authorize(headers.getFirst("Authorization"), entityId);
    }

    public abstract AuthorizationResult authorize(String jwtToken, String entityId);

    public abstract AuthorizationResult authenticate(String jwtToken, String entityId);

}
