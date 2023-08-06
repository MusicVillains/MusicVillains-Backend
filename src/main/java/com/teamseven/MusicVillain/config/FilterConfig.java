package com.teamseven.MusicVillain.config;

import com.teamseven.MusicVillain.SeucrityFilter.MyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MyFilter> myFilterFilterRegistration(){
        FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>(new MyFilter());
        registrationBean.addUrlPatterns("/*"); // 모든 url에 대해서 필터 적용
        registrationBean.setOrder(1); // 필터 순서 지정, 숫자가 낮을수록 먼저 실행됨
        return registrationBean;
    }
}
