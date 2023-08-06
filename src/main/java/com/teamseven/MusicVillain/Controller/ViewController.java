package com.teamseven.MusicVillain.Controller;


import com.teamseven.MusicVillain.Entity.Member;
import com.teamseven.MusicVillain.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class ViewController {

    private final MemberService memberService;

    @Autowired
    public ViewController(MemberService memberService){
        this.memberService = memberService;
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

}