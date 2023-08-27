package com.teamseven.MusicVillain.Security.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Security.UserDetailsImpl;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Security.LoginAttemptMember;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

// JWT를 이용한 인증을 위한 필터
// UsernamePasswordAuthenticationFilter는 [POST] /login 요청이 들어오면 동작하는 필터로 username과 password를 받아서 인증을 진행한다.

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // /login 으로 들어온 요청의 username과 password를 받아서 실제 인증을 수행하는 Filter

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.memberRepository = memberRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JWT Authentication Filter is called, 로그인 시도중");

        System.out.println("=====================================");
        // username, password 확인, json에 대한 처리만 구현하였으므로
        // 프론트쪽에서 json 형식으로만 로그인하도록 해야함
        try{
            // ObjectMapper는 JSON 형식의 문자열을 객체로 변환해주는 역할을 한다.
            ObjectMapper om = new ObjectMapper();
            LoginAttemptMember loginAttemptMember = om.readValue(request.getInputStream(), LoginAttemptMember.class);

            System.out.println("loginAttemptMember.username: "+loginAttemptMember.username);
            System.out.println("loginAttemptMember.password: "+loginAttemptMember.password);


            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginAttemptMember.username,
                    loginAttemptMember.password
            );
            // authenticationManager.authenticate가 호출되면서 UserDetailsService의 loadUserByUsername 함수가 실행된다.
            // authentication에 로그인한 유저의 정보가 담긴다. authentication 객체는 session에 저장된다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken); // 로그인 시도

            // 로그인한 Member 정보 출력
            UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
            // 아래 정보가 출력되면 로그인이 성공한 것을 의미
            System.out.println("로그인 성공한 유저의 아이디: " + details.getMember().getName());

            // attemptAuthentication이 성공하면 successfulAuthentication 함수가 실행된다.
            // successfulAuthentication 함수에서 JWT 토큰을 생성하여 response의 header에 넣어주도록 구현한다.
            return authentication;
            }


        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("=====================================");


//        return super.attemptAuthentication(request, response);
        return null;
    }

    // attemptAuthentication 함수에서 인증이 성공하면 successfulAuthentication 함수가 실행된다. 즉 여기까지 진입하면 인증이 완료되었음을 의미한다.
    // 여기서 jwt 토큰을 생성하여 response의 header에 넣어 반환함
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        Member memberInUserDetails = userDetails.getMember();

        // JWT Token 발행
        String generateJwtToken = JwtManager.generateAccessToken(
                memberInUserDetails.getMemberId(),
                memberInUserDetails.getUserId(),
                memberInUserDetails.getRole());

        response.addHeader("Authorization", "Bearer "+generateJwtToken);

        // 로그인 성공시  response body에 아래 json 형식으로 응답
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\n"+
                "\t\"statusCode\": \"200\",\n" +
                "\t\"message\": \"Login Success\",\n" +
                "\t\"data\": {\n"+
                "\t\t\"memberId\": \""+ userDetails.getMember().getMemberId() +"\",\n"+
                "\t\t\"userId\": \""+ userDetails.getMember().getUserId().toString() +"\",\n"+
                "\t\t\"userName\": \""+ userDetails.getMember().getName().toString() +"\""+
                "\n\t}\n" +
                "}");
    }

    // 인증 실패시 호출되는 함수
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("Authentication Fail!");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\n" +
                "\t\"statusCode\": \"401\",\n" +
                "\t\"message\": \"Login Failed\"\n}");
    }
}
