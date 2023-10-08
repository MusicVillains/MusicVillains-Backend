package com.teamseven.MusicVillain.Aspect;

import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Exception.JwtAuthorizationFailException;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@Aspect
public class JwtAuthorizationAspect {

    @Before("@annotation(JwtAuthorizationRequired)")
    public void verifyAccessToken(JoinPoint joinPoint) throws Throwable {

        // delegate token validation
        ServiceResult tokenValidationResult = delegateTokenValidation();

        // if token validation failed, throw JwtAuthorizationFailException
        if (tokenValidationResult.isFailed()) {
            log.warn("Authorization Fail - {} ", tokenValidationResult.getMessage());
            throw new JwtAuthorizationFailException(tokenValidationResult.getMessage());
        }
    }


    public ServiceResult delegateTokenValidation() throws JwtAuthorizationFailException {
        // get Http Request
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // get Authorization Header
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.trace("Authorization Header: {}", authorizationHeader);

        // delegate Token Validation to JwtManager
        ServiceResult verifyAccessTokenResult = JwtManager.verifyAccessToken(authorizationHeader);
        log.trace("verifyAccessTokenResult: {}", verifyAccessTokenResult);

        return JwtManager.verifyAccessToken(authorizationHeader);
    }
}