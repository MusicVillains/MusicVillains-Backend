package com.teamseven.MusicVillain.Controller;


import com.teamseven.MusicVillain.DTO.MemberRequestDto;
import com.teamseven.MusicVillain.Entity.Member;
import com.teamseven.MusicVillain.Resposne.ResponseDto;
import com.teamseven.MusicVillain.Service.MemberService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


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


    @GetMapping("/view/members")
    public String memberView(Model model) {
        List<Member> memberList = memberService.getAllMembers();
        model.addAttribute("memberList", memberList);
        return "member_view";
    }

    @GetMapping("/dev/loginForm")
    public String loginForm(){
        return "loginForm";
    }
    @GetMapping("/dev/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/dev/joinFormHandler")
    @ResponseBody
    public ResponseDto joinFormHandler(MemberRequestDto memberRequestDto){
        return memberController.insertMember(memberRequestDto);
    }

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