package com.teamseven.MusicVillain.Dto.RequestBody;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseStatus;

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
