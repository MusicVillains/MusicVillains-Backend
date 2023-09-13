package com.teamseven.MusicVillain.Security.OAuth;

import lombok.Data;

@Data
public class LoginSuccessResponseBody {
    public String memberId;
    public String accessToken;
    public String refreshToken;
}
