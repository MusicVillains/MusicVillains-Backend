package com.teamseven.MusicVillain.Security.config;

import com.teamseven.MusicVillain.Security.Filter.CustomRequestCatchFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CustomRequestCatchFilter> myFilterFilterRegistration(){
        FilterRegistrationBean<CustomRequestCatchFilter> registrationBean = new FilterRegistrationBean<>(new CustomRequestCatchFilter());
        registrationBean.addUrlPatterns("/*"); // 모든 url에 대해서 필터 적용
        registrationBean.setOrder(1); // 필터 순서 지정, 숫자가 낮을수록 먼저 실행됨
        return registrationBean;
    }
}
