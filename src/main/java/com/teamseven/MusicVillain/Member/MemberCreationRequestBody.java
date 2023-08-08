package com.teamseven.MusicVillain.Member;

import lombok.Data;

@Data
public class MemberCreationRequestBody {
    public String userId;
    public String userInfo;
    public String name;
    public String email;


    public boolean hasNullField(){
        if(userId == null || userInfo == null || name == null || email == null){
            return true;
        }
        return false;
    }
}
