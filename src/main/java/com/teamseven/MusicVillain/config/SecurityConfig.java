package com.teamseven.MusicVillain.config;

import com.teamseven.MusicVillain.Repository.MemberRepository;
import com.teamseven.MusicVillain.SeucrityFilter.JwtAuthenticationFilter;
import com.teamseven.MusicVillain.SeucrityFilter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final MemberRepository memberRepository;

    @Autowired
    public SecurityConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        // csrf 해제
        http.csrf(csrf -> csrf.disable())

        .cors(cors -> cors.disable()) // cors 해제

        //.addFilter(corsFilter) // 구현한 corsFilter 추가해주어서 cors 해제, security를 통해 인증이 필요한 요청은 @CrossOrigin 어노테이션으로는 Cors 해제 불가능하기 때문에 여기서 걸어줘야함
                // 세션 사용 안하도록 설정
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // form login 사용 안함
                .formLogin((formLogin) -> formLogin.disable())
                // http Basic도 사용 안함
                .httpBasic((httpBasic) -> httpBasic.disable())
                .apply(new MyCustomDsl()); // 커스텀 필터 등록

                // 요청에 따른 권한 설정
                http.authorizeHttpRequests((authorize) -> authorize
                                .anyRequest().permitAll()
//                        .requestMatchers("/", "/welcome","/view/**","/dev/**").permitAll() // 모든 사용자 접근 가능
//                        .requestMatchers("/user").hasAnyRole("USER", "MANAGER", "ADMIN") // USER, MANAGER, ADMIN 접근 가능
//                        .requestMatchers("/manager").hasAnyRole("MANAGER", "ADMIN") // MANAGER, ADMIN 접근 가능
//                        .requestMatchers("/admin").hasRole("ADMIN") // ADMIN 접근 가능
                );

        return http.build();
    }


    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, memberRepository))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository));
        }
    }
}