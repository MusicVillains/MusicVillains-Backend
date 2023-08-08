package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.ENV;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println("[DEBUG] OAuth2AuthenticationFailureHandler.onAuthenticationFailure() Entered");
        String redirectURL = ENV.LOGGIN_FAILURE_REDIRECT_URL();
        System.out.println(">>> Redirect URL: " + redirectURL);
        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}
