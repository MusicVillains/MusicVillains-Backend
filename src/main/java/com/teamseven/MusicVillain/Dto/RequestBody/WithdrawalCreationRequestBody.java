package com.teamseven.MusicVillain.Dto.RequestBody;

import lombok.Data;

@Data
public class WithdrawalCreationRequestBody {
    public String reason;

    public boolean hasNullField(){
        if(reason == null){
            return true;
        }
        return false;
    }
}
