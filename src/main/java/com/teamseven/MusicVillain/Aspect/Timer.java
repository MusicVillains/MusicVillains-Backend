package com.teamseven.MusicVillain.Aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see com.teamseven.MusicVillain.Aspect.TimerAspect
 * @apiNote
 * TimerAspectJ에서 사용하기 위한 Marker Annotation.<br>
 * 수행 시간을 측정하고자하는 메서드에 정의하면 TimerAspectJ에 의해 수행 시간이 로그로 출력된다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Timer {
}
