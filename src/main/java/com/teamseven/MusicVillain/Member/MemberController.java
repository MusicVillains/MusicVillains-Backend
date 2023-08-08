package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.ResponseDto;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseDto insertMember(@RequestBody MemberCreationRequestBody memberCreationRequestBody){
//        System.out.println("entered insertMember");
//        System.out.println("memberReq.getuserId: " + memberRequestDto.getUserId());
//        System.out.println("memberReq.getuserInfo: " + memberRequestDto.getUserInfo());
//        System.out.println("memberReq.getname: " + memberRequestDto.getName());
//        System.out.println("memberReq.getemail: " + memberRequestDto.getEmail());
        Status s = memberService.insertMember(memberCreationRequestBody);

        return ResponseDto.builder().statusCode(s.getStatusCode()).message(s.getMessage()).build();
    }



}
