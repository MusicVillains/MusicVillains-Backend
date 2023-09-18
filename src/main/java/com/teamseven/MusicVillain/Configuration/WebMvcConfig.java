package com.teamseven.MusicVillain.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 허용할 오리진들을 설정합니다. 여기서는 모든 오리진을 허용합니다.
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")  // 허용할 HTTP 메서드를 설정합니다.
                .allowedHeaders("*")  // 허용할 헤더들을 설정합니다.
                .allowCredentials(true);  // 쿠키를 허용합니다.
    }
}