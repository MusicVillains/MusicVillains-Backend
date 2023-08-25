package com.teamseven.MusicVillain.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ENV{
    public final static Boolean IS_LOCAL = false;

    // OAuth
    public static String KAKAO_CLIENT_ID;
    public final static String BASE_URL = "http://localhost:8080";
    public final static String SPRING_PORT = "8080";
    public final static String LOCAL_LOGGIN_SUCCESS_REDIRECT_URL = BASE_URL + "/dev/loginSuccess"; // temporary
    public final static String FRONT_SERVER_LOGIN_SUCCESS_REDIRECT_URL = "http://localhost:3000/main"; //temporary
        //"https://team7-frontend-git-main-beside-team7.vercel.app/main";
    public final static String LOCAL_LOGGIN_FAILURE_REDIRECT_URL = BASE_URL + "/dev/loginFailure"; // temporary
    public final static String FRONT_SERVER_FAILURE_REDIRECT_URL = "http://localhost:3000/sign"; // temporary
            //"https://team7-frontend-git-main-beside-team7.vercel.app/"; // temporary

    /* For JWT and Secure */
    public final static Boolean AUTHORIZE_MODE = false;
    public static  String JWT_SECRET_KEY;
    public static int JWT_TOKEN_EXPIRE_TIME;
    public static String JWT_TOKEN_SUBJECT;
    public static final String JWT_TOKEN_PREFIX = "Bearer "; // 토큰 앞에 붙는 prefix
    public static String LOGGIN_SUCCESS_REDIRECT_URL(){
        if (IS_LOCAL) return LOCAL_LOGGIN_SUCCESS_REDIRECT_URL;
        return FRONT_SERVER_LOGIN_SUCCESS_REDIRECT_URL;
    }

    public static String LOGGIN_FAILURE_REDIRECT_URL(){
        if (IS_LOCAL) return LOCAL_LOGGIN_FAILURE_REDIRECT_URL;
        return FRONT_SERVER_FAILURE_REDIRECT_URL;
    }

    public static String JWT_SECRET_KEY(){
        return JWT_SECRET_KEY;
    }

    public static int JWT_TOKEN_EXPIRE_TIME(){
        return JWT_TOKEN_EXPIRE_TIME;
    }

    public static String JWT_TOKEN_SUBJECT(){
        return JWT_TOKEN_SUBJECT;
    }

    @Value("${env.secret.jwt.key}")
    public void SET_JWT_SECRET_KEY(String key){
        this.JWT_SECRET_KEY = key;
    }

    @Value("${env.secret.jwt.subject}")
    public void SET_JWT_TOKEN_SUBJECT(String subject){
        this.JWT_TOKEN_SUBJECT = subject;
    }

    @Value("${env.secret.jwt.token-expire-time}")
    public void SET_JWT_TOKEN_EXPIRE_TIME(int expireTime){
        this.JWT_TOKEN_EXPIRE_TIME = expireTime;
    }

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    public void SET_KAKAO_CLIENT_ID(String kakaoClientId){this.KAKAO_CLIENT_ID = kakaoClientId;}
}
