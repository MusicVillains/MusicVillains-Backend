package com.teamseven.MusicVillain.SeucrityFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("MyFilter is called");

        // HttpServletRequest, HttpServletResponse 로 다운 캐스팅
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println("RequestURI: " + req.getRequestURI());

        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println("Response: " + res.getStatus());

        // POST method인 경우, 필터에 들어온 HttpServletRequest 에서 Authorization 헤더 가져온 후 확인
        if(req.getMethod().equals("POST")){
        System.out.println("Method: " + req.getMethod() );
        String header_authorization = req.getHeader("Authorization");
        System.out.println("Authorization: " + header_authorization);
        }

        chain.doFilter(request, response); // 다음 필터로 넘어감
    }
}
