package com.teamseven.MusicVillain;

public class ENV{
    public final static Boolean IS_LOCAL = false;

    public final static String BASE_URL = "http://localhost:8080";
    public final static String SPRING_PORT = "8080";
    public final static String FRONT_SERVER_LOGIN_SUCCESS_REDIRECT_URL = "https://team7-frontend-git-main-beside-team7.vercel.app/main";
    public final static String LOCAL_LOGGIN_SUCCESS_REDIRECT_URL = BASE_URL + "/dev/loginSuccess"; // temporary

    public static String LOGGIN_SUCCESS_REDIRECT_URL(){
        if (IS_LOCAL) return LOCAL_LOGGIN_SUCCESS_REDIRECT_URL;
        return FRONT_SERVER_LOGIN_SUCCESS_REDIRECT_URL;
    }

}
