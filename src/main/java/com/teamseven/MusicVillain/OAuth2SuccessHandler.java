package com.teamseven.MusicVillain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.teamseven.MusicVillain.Entity.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        System.out.println("[DEBUG] OAuth2SuccessHandler.onAuthenticationSuccess() Entered");
        //String redirectURL =  "http://localhost:8080/OAuth/redirectAfterLogin";
        String redirectURL = "http://localhost:8080/dev/loginSuccess";
        String jwtToken = JWT.create().withSubject("wdyToken") // 토큰 발행자
                .withExpiresAt(new Date(System.currentTimeMillis()+ (60000*10))) // 토큰 만료 시간, 10분으로 설정
                // 토큰에 담을 정보는 withClaim으로 담는다. 정해져있는 것은 아니고 넣고싶은거 설정해주면 됨
                .withClaim("id", ((UserDetailsImpl)authentication.getPrincipal()).getMember().memberId) // 토큰에 담을 정보
                .withClaim("username",((UserDetailsImpl)authentication.getPrincipal()).getMember().getUserId()) // 토큰에 담을 정보
                .sign(Algorithm.HMAC512(JwtVariables.JWT_SECRETE)); // 토큰 암호화 알고리즘, secret key 넣어줘야 함

        response.addHeader("Authorization", "Bearer "+jwtToken);
        // JWT token 발행 완료
        System.out.println(">>> JWT Token 발행 완료: " + jwtToken);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        String ResponseJsonString = "{\n"+
                "\t\"statusCode\": \"200\",\n" +
                "\t\"token\": \"Bearer "+ jwtToken + "\",\n" +
                "}";
        response.getWriter().write(ResponseJsonString);
        System.out.println(">>> Response Json String: " + ResponseJsonString);
        System.out.println(">>> RedirectURL: " + redirectURL);
        getRedirectStrategy().sendRedirect(request, response, redirectURL); // Response 전송하고, 로그인 후 이동해야할 프론트 페이지로 redirect
        //super.onAuthenticationSuccess(request, response, authentication);
    }
}