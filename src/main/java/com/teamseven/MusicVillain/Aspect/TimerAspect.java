package com.teamseven.MusicVillain.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimerAspect {

    // @Around("execution(* com.teamseven.MusicVillain.*.*Controller.*(..))")
    @Around("@annotation(Timer)")
    public Object timeCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.debug("result: {} " , result.getClass());
        log.debug("\n"
                + "- 수행 메서드: "
                + proceedingJoinPoint.getSignature().getDeclaringTypeName()
                + "." + proceedingJoinPoint.getSignature().getName() + "\n"
                + "- 수행 시간 : " + ((endTime - startTime) / 1000.0) + " sec");
        return result;
    }
}
