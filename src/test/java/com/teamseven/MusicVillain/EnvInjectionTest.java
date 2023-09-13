package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Utils.ENV;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

@SpringBootTest

/**
 * `@TestClassOrder`: nested class 단위로 order 설정을 적용해주기 위해서 사용 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) /* 테스트 클래스 내의 메소드간 변수 공유를 위함 */
@Slf4j
@DisplayName("환경변수 주입 및 초기화 테스트")

public class EnvInjectionTest{


    @Nested
    @Order(1)
    @DisplayName("환경변수 주입 테스트")
    class InjectionTest{

        @Nested
        @DisplayName("JWT 환경 변수 주입 테스트")
        class JwtVariableTest {

            @Test
            @DisplayName("`JWT_SECRET_KEY` 주입 테스트")
            void JwtSecretKeyInjectionTest() {
                String jwtSecretKey = ENV.JWT_SECRET_KEY;
                log.debug("JWT_SECRET_KEY: {}", jwtSecretKey);
                Assertions.assertNotNull(jwtSecretKey);
            }

            @Test
            @DisplayName("`JWT_TOKEN_SUBJECT` 주입 테스트")
            void JwtTokenSubjectInjectionTest() {
                String jwtTokenSubject = ENV.JWT_TOKEN_SUBJECT;
                log.debug("JWT_TOKEN_SUBJECT: {}", jwtTokenSubject);
                Assertions.assertNotNull(jwtTokenSubject);
            }

            @Test
            @DisplayName("`JWT_ACCESS_TOKEN_EXPIRE_TIME` 주입 테스트")
            void JwtAccessTokenExpireTimeInjectionTest() {
                Long jwtAccessTokenExpireTime = ENV.JWT_ACCESS_TOKEN_EXPIRE_TIME;
                log.debug("JWT_ACCESS_TOKEN_EXPIRE_TIME: {}", jwtAccessTokenExpireTime);
                Assertions.assertNotNull(jwtAccessTokenExpireTime);
            }

            @Test
            @DisplayName("`JWT_REFRESH_TOKEN_EXPIRE_TIME` 주입 테스트")
            void JwtRefreshTokenExpireTimeInjectionTest() {
                Long jwtRefreshTokenExpireTime = ENV.JWT_REFRESH_TOKEN_EXPIRE_TIME;
                log.debug("JWT_REFRESH_TOKEN_EXPIRE_TIME: {}", jwtRefreshTokenExpireTime);
                Assertions.assertNotNull(jwtRefreshTokenExpireTime);
            }


        }

        @Nested
        @DisplayName("OAuth 환경 변수 주입 테스트")
        class OAuthVaraibleInjectionTest {
            @Test
            @DisplayName("`KAKAO_CLIENT_ID` 주입 테스트")
            void kakaoClientIdInjectionTest() {
                log.debug("KAKAO_CLIENT_ID: {}", ENV.KAKAO_CLIENT_ID);
                Assertions.assertNotNull(ENV.KAKAO_CLIENT_ID);
            }

            @Test
            @DisplayName("`KAKAO_CLIENT_SECRET` 주입 테스트")
            void kakaoClientSecretInjectionTest() {
                log.debug("KAKAO_CLIENT_SECRET: {}", ENV.KAKAO_CLIENT_SECRET);
                Assertions.assertNotNull(ENV.KAKAO_CLIENT_SECRET);
            }

            @Test
            @DisplayName("`KAKAO_REDIRECT_ID` 주입 테스트")
            void kakaoRedirectIdInjectionTest() {
                log.debug("KAKAO_REDIRECT_URI: {}", ENV.KAKAO_REDIRECT_URI);
                Assertions.assertNotNull(ENV.KAKAO_REDIRECT_URI);
            }
        }
    }

    @Nested
    @Order(2)
    @DisplayName("환경변수 값 초기화 테스트")
    class EnvNotNullTest{
        @Test
        @DisplayName("`IS_LOCAL` 초기화 테스트")
        void isLocalNotNullTest(){
            log.debug("IS_LOCAL: {}", ENV.IS_LOCAL);
            Assertions.assertNotNull(ENV.IS_LOCAL);
        }

        @Test
        @DisplayName("`JWT_PREFIX` 초기화 테스트")
        void JwtPrefixNotNullTest(){
            var jwtPrefix = ENV.JWT_TOKEN_PREFIX;
            log.debug("JWT_PREFIX: {}", jwtPrefix);
            Assertions.assertNotNull(jwtPrefix);
        }
    }
}