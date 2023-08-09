package com.teamseven.MusicVillain.Security.Filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Security.UserDetailsImpl;
import com.teamseven.MusicVillain.JwtVariables;
import com.teamseven.MusicVillain.Member.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// Spring Security Filter 중에 BasicAuthenticationFilter 라는 것이 있음
// 권한 또는 인증이 필요한 특정 주소를 요청하면 위 필터를 무조건 타게 된다.
// 만약 권한 또는 인증이 필요하지 않으면 해당 필터를 타지 않음
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;


    @Autowired
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("[DEBUG] Enter JwtAuthorizationFilter.doFilterInternal()");
//        System.out.println(">>> 인증이나 권한 요청이 필요한 주소요청이 있을 때 해당 필터를 타게 된다.");
        String jwtHeader = request.getHeader("Authorization");
//        System.out.println("jwtHeader: "+jwtHeader);

        // jwt Header 양식이 맞는지 확인
//        System.out.println(">>> jwt Header 양식이 맞는지 확인");
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")){ // 토큰이 없거나 Bearer로 시작하지 않으면 검증하지 않고 다음 필터로 넘어감
            chain.doFilter(request, response); // 다음 필터로 넘어감
//            System.out.println(">>>>>> jwt Header 양식이 맞지 않음");
            return;
        }
//        System.out.println(">>>>>> jwt Header 양식 정상");
        // jwt Header 양식이 맞으면 검증해서 정상적인 사용자인지 확인

//        System.out.println(">>> 검증해서 정상적인 사용자인지 확인");

        String jwtToken = request.getHeader("Authorization").replace("Bearer ", ""); // Bearer 없애고, 뒤에 있는 토큰값만 가져옴
        String username = "";
        try {
             username = JWT.require(Algorithm.HMAC512(JwtVariables.JWT_SECRETE)).build() // 토큰 생성 시 사용했던 암호화 방식을 적용
                    .verify(jwtToken) // 토큰 검증
                    .getClaim("username").asString(); // username claim을 가져옴
        }
        catch(TokenExpiredException e) {
//            System.out.println(">>>>>> 토큰 만료 Exception 발생");
            e.printStackTrace();
            chain.doFilter(request, response);
            return;
        }


//        System.out.println(">>>> **** username: "+username);

        if(username != null){
//            System.out.println(">>>>>> 사용자 존재, 검증 성공");

            Member member = memberRepository.findByUserId(username);

//            System.out.println(">>>> Token is valid. username: "+ member.getUserId());

            UserDetailsImpl userDetails = new UserDetailsImpl(member);

            userDetails.getAuthorities().forEach(s ->
                    System.out.println(s.getAuthority().toString()));

            // 올바른 토큰이면 Authentication 객체를 만들어서 SecurityContext에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken
                    (userDetails, null,userDetails.getAuthorities());

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        else {
//            System.out.println(">>>>>> 존재하지 않는 사용자, 검증 실패");
        }
        //super.doFilterInternal(request, response, chain);
        chain.doFilter(request, response);
    }
}
