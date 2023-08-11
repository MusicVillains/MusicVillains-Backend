package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Member.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationResult {
    public String result;
    public String message;
    public Member member;

    public static final String FAIL = "Authentication failed.";
    public static final String SUCCESS = "Authentication succeeded.";

    public AuthorizationResult(String result, String message, Member member){
        this.result = result;
        this.message = message;
        this.member = member;
    }

    public static AuthorizationResult success(String message, Member member){
        return new AuthorizationResult(SUCCESS, message, member);
    }


    public static AuthorizationResult success(Member member){
        return new AuthorizationResult(SUCCESS, SUCCESS, member);
    }


    public static AuthorizationResult fail(){
        return new AuthorizationResult(FAIL, FAIL, null);
    }

    public static AuthorizationResult fail(String message){
        return new AuthorizationResult(FAIL, message, null);
    }


    public boolean isFailed(){
        return this.result.equals(FAIL);
    }

    public boolean isSuccessful(){
        return this.result.equals(SUCCESS);
    }
}