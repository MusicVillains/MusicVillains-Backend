package com.teamseven.MusicVillain.Security.config;

import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Security.Filter.JwtAuthenticationFilter;
import com.teamseven.MusicVillain.Security.Filter.JwtAuthorizationFilter;
import com.teamseven.MusicVillain.Security.OAuth.OAuth2AuthenticationFailureHandler;
import com.teamseven.MusicVillain.Security.OAuth.OAuth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.teamseven.MusicVillain.Security.OAuth.OAuth2AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
    private final MemberRepository memberRepository;
    @Bean
    public OAuth2UserServiceImpl oAuth2UserServiceImpl(){
        return new OAuth2UserServiceImpl(memberRepository);
    };

    @Bean
    @Autowired
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(){
        return new OAuth2AuthenticationSuccessHandler();
    }

    @Bean
    @Autowired
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler(){
        return new OAuth2AuthenticationFailureHandler();
    }
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
        http.addFilter(new CorsConfig().corsFilter());
        // csrf 해제
        http.csrf(csrf -> csrf.disable())

        .cors(cors -> cors.disable()) // cors 해제

        //.addFilter(corsFilter) // 구현한 corsFilter 추가해주어서 cors 해제, security를 통해 인증이 필요한 요청은 @CrossOrigin 어노테이션으로는 Cors 해제 불가능하기 때문에 여기서 걸어줘야함
                // 세션 사용 안하도록 설정
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER)) // JWT 인증 방식이 세션을 사용하지 않지만 OAuth 사용할 시 필요하기 때문에 NEVER로 설정 설정
                // NEVER 옵션은 세션을 사용하진 않지만 만약 필요하다면 세션을 생성해주는 옵션

                // OAuth2 로그인 사용, 성공 시 /OAuth/success로 이동. 이 때, 해당 API를 통해 토큰을 Response로 보낼 예정
                .oauth2Login((oauth2Login) -> oauth2Login
                        .userInfoEndpoint((userInfoEndpoint) -> userInfoEndpoint
                                //oauth2Login을 진행하게 되면 loadUser라는 함수를 호출하게 되는데 이것을 OAuth2UserServiceImpl에서 호출하겠다는 설정.
                                .userService(oAuth2UserServiceImpl())) // 로그인 성공 시 후속 조치를 진행할 OAuth2UserService 인터페이스의 구현체 등록
                        /*.successHandler(oAuth2AuthenticationSuccessHandler()) // [!] Temporarily disabled
                          .failureHandler(oAuth2AuthenticationFailureHandler()) */ // [!] Temporarily disabled
                        //.defaultSuccessUrl("/OAuth/loginSuccess")
                        //.failureUrl("/OAuth/loginFailure")) //  실패 시 /OAuth/loginFailure 호출
                )

                // form login 사용 안함
                .formLogin((formLogin) -> formLogin.disable())
                // http Basic도 사용 안함
                .httpBasic((httpBasic) -> httpBasic.disable())
                .apply(new MyCustomDsl()); // 커스텀 필터 등록

                // 요청에 따른 권한 설정
                http.authorizeHttpRequests((authorize) -> authorize
                                //.anyRequest().permitAll()
                         //.requestMatchers("/", "/welcome","/view/**","/dev/**").permitAll() // 모든 사용자 접근 가능
                          //.requestMatchers("/user").hasAnyRole("USER", "MANAGER", "ADMIN") // USER, MANAGER, ADMIN 접근 가능
                          //.requestMatchers("/manager").hasAnyRole("MANAGER", "ADMIN") // MANAGER, ADMIN 접근 가능
                          .anyRequest().permitAll()
//                        .requestMatchers("/admin").hasRole("ADMIN") // ADMIN 접근 가능
                );


                http.exceptionHandling((exceptionHandling) -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/access-denied");
                        })
                        .authenticationEntryPoint((request, response, authenticationException) -> {
                            response.sendRedirect("/login");
                        })
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