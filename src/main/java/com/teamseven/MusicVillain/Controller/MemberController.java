package com.teamseven.MusicVillain.Controller;

import com.teamseven.MusicVillain.DTO.MemberRequestDto;
import com.teamseven.MusicVillain.Entity.Member;
import com.teamseven.MusicVillain.Resposne.ResponseDto;
import com.teamseven.MusicVillain.Service.MemberService;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }


    @GetMapping("/members")
    public List<Member> members(){
        List<Member> memberList = memberService.getAllMembers();
        return memberList;
    }

    @PostMapping("/members")
    public ResponseDto insertMember(@RequestBody MemberRequestDto memberRequestDto){
//        System.out.println("entered insertMember");
//        System.out.println("memberReq.getuserId: " + memberRequestDto.getUserId());
//        System.out.println("memberReq.getuserInfo: " + memberRequestDto.getUserInfo());
//        System.out.println("memberReq.getname: " + memberRequestDto.getName());
//        System.out.println("memberReq.getemail: " + memberRequestDto.getEmail());
        Status s = memberService.insertMember(memberRequestDto);

        return ResponseDto.builder().statusCode(s.getStatusCode()).message(s.getMessage()).build();
    }



}
