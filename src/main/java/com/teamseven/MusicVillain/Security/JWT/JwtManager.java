package com.teamseven.MusicVillain.Security.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.teamseven.MusicVillain.Dto.Converter.DateConverter;
import com.teamseven.MusicVillain.Utils.ENV;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import java.util.Date;
@Slf4j
public class JwtManager {

    /* ────────────────────────  Constant ────────────────────── */
    public static final String TYPE_ACCESS_TOKEN(){
        return "access";
    }

    public static final String TYPE_REFRESH_TOKEN(){
        return "refresh";
    }

    /* ──────────────────────── Generate Token ────────────────────── */
    public static String generateAccessToken(String memberId, String userId, String role) {
        String generatedAccessToken = JWT.create().withSubject(ENV.JWT_TOKEN_SUBJECT()) // 토큰 발행자
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*ENV.JWT_ACCESS_TOKEN_EXPIRE_TIME))) // 토큰 만료 시간
                // 토큰에 담을 정보는 withClaim으로 담는다. 정해져있는 것은 아니고 넣고싶은거 설정해주면 됨
                .withClaim("type", "access") // 토큰에 담을 정보
                .withClaim("memberId", (memberId))// 토큰에 담을 정보
                .withClaim("userId",(userId)) // 토큰에 담을 정보
                .withClaim("role", (role)) // 토큰에 담을 정보
                .sign(Algorithm.HMAC512(ENV.JWT_SECRET_KEY())); // 토큰 암호화 알고리즘, secret key 넣어줘야 함

        DecodedJWT decoded = JWT.decode(generatedAccessToken);
        log.trace("Access Token issued\n" +
                "\t-type: {}\n" +
                "\t-memberId: {}\n" +
                "\t-userId: {}\n" +
                "\t-role: {}\n" +
                "\t-expireAt: {}",
                decoded.getClaim("type").asString(),
                decoded.getClaim("memberId").toString(),
                decoded.getClaim("userId").toString(),
                decoded.getClaim("role").toString(),
                DateConverter.convertDateToLocalDateTime(decoded.getExpiresAt()));

        return generatedAccessToken;

    }

    public static String generateRefreshToken(String memberId, String userId, String role) {
        String generatedRefreshToken = JWT.create().withSubject(ENV.JWT_TOKEN_SUBJECT())
                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*ENV.JWT_REFRESH_TOKEN_EXPIRE_TIME))) // 토큰 만료 시간
                .withClaim("type", "refresh")
                .withClaim("memberId", (memberId))
                .withClaim("userId",(userId))
                .withClaim("role", (role))
                .sign(Algorithm.HMAC512(ENV.JWT_SECRET_KEY()));

        DecodedJWT decoded = JWT.decode(generatedRefreshToken);
        log.trace("Refresh Token issued\n" +
                        "\t-type: {}\n" +
                        "\t-memberId: {}\n" +
                        "\t-userId: {}\n" +
                        "\t-role: {}\n" +
                        "\t-expireAt: {}",
                decoded.getClaim("type").asString(),
                decoded.getClaim("memberId").toString(),
                decoded.getClaim("userId").toString(),
                decoded.getClaim("role").toString(),
                DateConverter.convertDateToLocalDateTime(decoded.getExpiresAt()));

        return generatedRefreshToken;
    }

    /* ──────────────────────── Verify Token ────────────────────── */
    public static ServiceResult verifyAccessToken(HttpHeaders requestHeaders){
        return verifyAccessToken(getAuthorizationFieldFromHttpHeaders(requestHeaders));
    }

    public static ServiceResult verifyAccessToken(String authorizationHeader){

        if (!isValidJwtHeaderFormat(authorizationHeader)) {
            return ServiceResult.of(ServiceResult.FAIL,
                    "Invalid Jwt Format",
                    null);
        }

        String jwtToken = authorizationHeader.replace
                ("Bearer ", ""); // remove "Bearer " from authorization header

        String tmpMemberId = "";
        try {
            tmpMemberId = JWT.require(Algorithm.HMAC512(ENV.JWT_SECRET_KEY())).build() // 토큰 생성 시 사용했던 암호화 방식을 적용
                    .verify(jwtToken) // 토큰 검증
                    .getClaim("memberId").asString(); // memberId claim을 가져옴
        }
        catch(ExpiredJwtException e){
            // occurs when jwt token is expired
            e.printStackTrace();
            return ServiceResult.of(ServiceResult.FAIL,
                    e.getMessage().toString(),
                    null);
        }
        catch(Exception e) {
            e.printStackTrace();
            return ServiceResult.of(ServiceResult.FAIL,
                    e.getMessage().toString(),
                    null);
        }

        return ServiceResult.of(ServiceResult.SUCCESS,
                "verifyToken() succeeded.",
                tmpMemberId);

    }

    /* ````````````````````````````````````````````````````````````` */


    /* ────────────────────────── Utils ─────────────────────────── */
    public static String getAuthorizationFieldFromHttpHeaders(HttpHeaders httpHeaders){
        return httpHeaders.getFirst("Authorization");
    }

    public static boolean isValidJwtHeaderFormat(String jwtToken){
        return jwtToken != null && jwtToken.startsWith(ENV.JWT_TOKEN_PREFIX);
    }
}
