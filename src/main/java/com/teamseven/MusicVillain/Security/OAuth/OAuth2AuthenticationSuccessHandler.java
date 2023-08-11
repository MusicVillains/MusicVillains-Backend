package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.ENV;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Security.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        System.out.println("[DEBUG] OAuth2SuccessHandler.onAuthenticationSuccess() Entered");
        //String redirectURL =  "http://localhost:8080/OAuth/redirectAfterLogin";
        System.out.println("[DEBUG] LOGGIN_SUCCESS_REDIRECT_URL: " + ENV.LOGGIN_SUCCESS_REDIRECT_URL());
        String redirectURL = ENV.LOGGIN_SUCCESS_REDIRECT_URL(); // 프론트쪽 로그인 성공 페이지로 리다이렉트

        Member memberInUserDetails = ((UserDetailsImpl)authentication.getPrincipal()).getMember();
        String generatedJwtToken = JwtManager.generateToken(
                memberInUserDetails.getMemberId(),
                memberInUserDetails.getUserId(),
                memberInUserDetails.getRole());

        response.addHeader("Authorization", "Bearer "+generatedJwtToken);
        // JWT token 발행 완료
        System.out.println(">>> JWT Token 발행 완료: " + generatedJwtToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        String ResponseJsonString = "{\n"+
                "\t\"statusCode\": \"200\",\n" +
                "\t\"token\": \"Bearer "+ generatedJwtToken + "\",\n" +
                "}";
        response.getWriter().write(ResponseJsonString);
        System.out.println(">>> Response Json String: " + ResponseJsonString);
        System.out.println(">>> Redirect URL: " + redirectURL);
        getRedirectStrategy().sendRedirect(request, response, redirectURL); // Response 전송하고, 로그인 후 이동해야할 프론트 페이지로 redirect
    }
}