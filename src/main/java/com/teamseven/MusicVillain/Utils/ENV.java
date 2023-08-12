package com.teamseven.MusicVillain.Utils;

public class ENV{
    public final static Boolean IS_LOCAL = false;

    public final static String BASE_URL = "http://localhost:8080";
    public final static String SPRING_PORT = "8080";
    public final static String LOCAL_LOGGIN_SUCCESS_REDIRECT_URL = BASE_URL + "/dev/loginSuccess"; // temporary
    public final static String FRONT_SERVER_LOGIN_SUCCESS_REDIRECT_URL = "https://team7-frontend-git-main-beside-team7.vercel.app/main";
    public final static String LOCAL_LOGGIN_FAILURE_REDIRECT_URL = BASE_URL + "/dev/loginFailure"; // temporary
    public final static String FRONT_SERVER_FAILURE_REDIRECT_URL = BASE_URL + "https://team7-frontend-git-main-beside-team7.vercel.app/"; // temporary


    /* For JWT and Secure */

    public final static Boolean AUTHORIZE_MODE = false;
    public final static String JWT_SECRET_KEY = "1a690b8c7ed346f8b2b49e7660f02232";
    public final static int JWT_TOKEN_EXPIRE_TIME = 30; // minutes
    public final static String JWT_TOKEN_SUBJECT = "BesideTeam7"; // 토큰 발행자
    public final static String JWT_TOKEN_PREFIX = "Bearer "; // 토큰 앞에 붙는 prefix
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

}
