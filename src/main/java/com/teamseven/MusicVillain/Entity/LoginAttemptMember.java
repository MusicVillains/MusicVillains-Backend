package com.teamseven.MusicVillain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginAttemptMember {
    // Spring Security 에서는 username과 password를 사용하기 때문에 로그인 Request Body에 들어있는 파라미터명을 맞춰서 받아주기 위한 클래스
    public String username;
    public String password;
}
