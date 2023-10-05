package com.teamseven.MusicVillain.Exception;

public class JwtAuthorizationFailException extends Exception{

    /**
     * @apiNote
     * JWT Authorization 실패시 발생하는 예외
     */
    public JwtAuthorizationFailException(String customMessage) {
        super(customMessage);
    }

}
