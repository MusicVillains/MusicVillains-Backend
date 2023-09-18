package com.teamseven.MusicVillain.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1")
                .title("MusicVillains API")
                .description("refrence for MusicVillains API");


        // SecuritySecheme 이름
        String jwtSchemeName = "Access Token(JWT)";

        // API호출 시 전역으로 설정한 인증정보가 요청 헤더에 포함이 되도록 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식으로 설정, HTTP 외에도 APIKEY, OAUTH2, OpenIdConnect 등 지원
                        .scheme("bearer") // bearer token 방식을 사용
                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

        return new OpenAPI()
                .servers(Arrays.asList(
                        new Server().url("https://musicvillains.run.goorm.io/")
                                .description("Production Server"),
                        new Server().url("http://localhost:8080")
                                .description("Local Server")
                ))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }


}