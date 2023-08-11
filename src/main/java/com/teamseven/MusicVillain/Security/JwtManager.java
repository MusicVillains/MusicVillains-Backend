package com.teamseven.MusicVillain.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.teamseven.MusicVillain.ENV;
import com.teamseven.MusicVillain.ServiceResult;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Date;

public class JwtManager {
    
    public static String generateToken(String memberId, String userId) {
        return JWT.create().withSubject(ENV.JWT_TOKEN_SUBJECT()) // 토큰 발행자
                .withExpiresAt(new Date(System.currentTimeMillis()+ (60000*ENV.JWT_TOKEN_EXPIRE_TIME))) // 토큰 만료 시간, 10분으로 설정
                // 토큰에 담을 정보는 withClaim으로 담는다. 정해져있는 것은 아니고 넣고싶은거 설정해주면 됨
                .withClaim("memberId", (memberId))// 토큰에 담을 정보
                .withClaim("userId",(userId)) // 토큰에 담을 정보
                .sign(Algorithm.HMAC512(ENV.JWT_SECRET_KEY())); // 토큰 암호화 알고리즘, secret key 넣어줘야 함
    }

    public static ServiceResult verify_token(String jwtToken){
        String tmpMemberId = "";
        try {
            tmpMemberId = JWT.require(Algorithm.HMAC512(ENV.JWT_SECRET_KEY())).build() // 토큰 생성 시 사용했던 암호화 방식을 적용
                    .verify(jwtToken) // 토큰 검증
                    .getClaim("memberId").asString(); // username claim을 가져옴
        }
        catch(ExpiredJwtException e){
            // occurs when jwt token is expired
            e.printStackTrace();
            return ServiceResult.of(ServiceResult.FAIL,
                    e.getMessage(),
                    null);
        }
        catch(Exception e) {
            e.printStackTrace();
            return ServiceResult.of(ServiceResult.FAIL,
                    e.getMessage(),
                    null);
        }

        return ServiceResult.of(ServiceResult.SUCCESS,
                "JwtManager.verify_token() Success",
                tmpMemberId);

    }

}
