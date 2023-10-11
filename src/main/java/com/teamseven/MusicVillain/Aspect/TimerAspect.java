package com.teamseven.MusicVillain.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @see com.teamseven.MusicVillain.Aspect.Timer
 * @apiNote
 * 메서드의 시작과 끝을 기록하여 총 수행 시간을 측정하고 logger로 출력해주는 AspectJ<br>
 * `@Timer` 어노테이션이 정의된 메서드에 적용된다.
 */
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
