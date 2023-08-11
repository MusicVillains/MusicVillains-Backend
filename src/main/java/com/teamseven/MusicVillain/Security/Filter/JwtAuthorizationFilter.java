package com.teamseven.MusicVillain.Security.Filter;

import com.teamseven.MusicVillain.ENV;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Security.UserDetailsImpl;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Dto.ServiceResult;
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

        String jwtHeader = request.getHeader("Authorization");

        if(jwtHeader == null || !jwtHeader.startsWith(ENV.JWT_TOKEN_PREFIX)){
            // 토큰이 없거나 Bearer로 시작하지 않으면 검증하지 않고 다음 필터로 넘어감
            chain.doFilter(request, response); // 다음 필터로 넘어감
            return;
        }

        String jwtToken = request.getHeader("Authorization").replace("Bearer ", ""); // Bearer 없애고, 뒤에 있는 토큰값만 가져옴
        ServiceResult result = JwtManager.verifyToken(jwtToken);

        if(result.isFailed()){
            chain.doFilter(request, response);
            return;
        }

        if(result.isSuccessful()){
            String memberIdInJwtToken = result.getData().toString();
            // if valid token
            Member member = memberRepository.findByMemberId(memberIdInJwtToken);
            UserDetailsImpl userDetails = new UserDetailsImpl(member);
            userDetails.getAuthorities().forEach(s ->
                    System.out.println(s.getAuthority().toString()));

            // 올바른 토큰이면 Authentication 객체를 만들어서 SecurityContext에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken
                    (userDetails, null,userDetails.getAuthorities());

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
