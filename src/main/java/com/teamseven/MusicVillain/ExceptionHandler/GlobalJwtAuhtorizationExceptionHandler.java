package com.teamseven.MusicVillain.ExceptionHandler;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Exception.JwtAuthorizationFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @apiNote
 * JWT Authorization 관련 예외들을 처리하는 핸들러<br>
 * 모든 @Controller 또는 @RestController에서 발생하는 JWT 인증/인가 예외에 대한 핸들 메서드를 제공한다.<br>
 * @author Woody K
 */
@Slf4j
@ControllerAdvice
public class GlobalJwtAuhtorizationExceptionHandler {
    /**
     * @apiNote
     * JwtAuthorizationFailException 발생 시 처리하기 위한 handle method
     * @return HTTP Status Code `UNAUTHORIZED(401)`와 실패 메시지를 담은 ResponseObject를 반환한다.
     * @author Woody K
     */
    @ExceptionHandler(JwtAuthorizationFailException.class)
    public ResponseObject JwtAuthorizationFailExceptionHandle(Exception e){
        return ResponseObject.UNAUTHORIZED("Authorization failed");
    }
}
