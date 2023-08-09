package com.teamseven.MusicVillain.Security.Filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomRequestCatchFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        // HttpServletRequest, HttpServletResponse 로 다운 캐스팅
        HttpServletRequest req = (HttpServletRequest) request;

        System.out.println("──────────────────────── Request Occur ────────────────────────");
        System.out.println("Time: " + LocalDateTime.now());
        if (((HttpServletRequest) request).getMethod()!= null)
        System.out.println("Method: " + req.getMethod().toString());
        if (request.getContentType() != null)
        System.out.println("ContentType: " + req.getContentType().toString());
        if (((HttpServletRequest) request).getRequestURL() != null)
            System.out.println("RequestURL: " + req.getRequestURL().toString());
        if (((HttpServletRequest) request).getQueryString() != null)
            System.out.println("QueryString: " + req.getQueryString().toString());

        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.println("");



//        HttpServletResponse res = (HttpServletResponse) response;
//        System.out.println("Response: " + res.getStatus());
//
//        // POST method인 경우, 필터에 들어온 HttpServletRequest 에서 Authorization 헤더 가져온 후 확인
//        if(req.getMethod().equals("POST")){
//        System.out.println("Method: " + req.getMethod() );
//        String header_authorization = req.getHeader("Authorization");
//        System.out.println("Authorization: " + header_authorization);
//        }

        chain.doFilter(request, response); // 다음 필터로 넘어감
    }
}
