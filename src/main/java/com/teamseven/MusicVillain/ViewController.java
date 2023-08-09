package com.teamseven.MusicVillain;


import com.teamseven.MusicVillain.Member.MemberCreationRequestBody;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberController;
import com.teamseven.MusicVillain.Member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")

@Controller
public class ViewController {

    private MemberController memberController;
    private final MemberService memberService;

    @Autowired
    public ViewController(MemberService memberService, MemberController memberController){
        this.memberService = memberService;
        this.memberController = memberController;
    }

    @GetMapping({"/", "/index"})
    public String index(){
        return "index"; // returns index.html
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