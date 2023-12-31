package com.teamseven.MusicVillain.View;

import com.teamseven.MusicVillain.Member.MemberController;
import com.teamseven.MusicVillain.Member.MemberService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Hidden
@Controller
//@Slf4j
public class ViewController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private MemberController memberController;
    private final MemberService memberService;

    @Autowired
    public ViewController(MemberService memberService, MemberController memberController){
        this.memberService = memberService;
        this.memberController = memberController;
    }

    @GetMapping({"/", "/index"})
    public String index(){
        return "redirect:/swagger-ui/index.html"; // returns index.html
    }

    @GetMapping("/dev/loginForm")
    public String loginForm(){
        return "loginForm";
    }
    @GetMapping("/dev/joinForm")
    public String joinForm(){
        return "joinForm";
    }

//    @PostMapping("/dev/joinFormHandler")
//    @ResponseBody
//    public ResponseDto joinFormHandler(MemberCreationRequestBody memberCreationRequestBody){
//        return memberController.insertMember(memberCreationRequestBody);
//    }

    @GetMapping("/dev/loginSuccess")
    public String loginSuccess(){
        return "loginSuccess";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(){
        return "login";
    }

    @GetMapping("/testLoginPage")
    public String testLoginPage(){
        return "restAPI_loginTestPage";
    }

}