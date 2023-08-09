package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.ResponseDto;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/members/{memberId}")
    public Member getMemberById(@PathVariable("memberId") String memberId){
        return memberService.getMemberById(memberId);
    }

    @PostMapping("/members")
    public ResponseDto insertMember(@RequestBody MemberCreationRequestBody memberCreationRequestBody){
        Status s = memberService.insertMember(memberCreationRequestBody);

        return ResponseDto.builder().statusCode(s.getStatusCode()).message(s.getMessage()).build();
    }



    /**
     * @Title: modifyMemberNickname
     * @Author: Woody K
     * @Date: 2021/10/10
     * @Description: 회원 정보 수정
     *
     * @Param memberId : 수정할 회원의 멤버 아이디
     * @Param nickname : 수정할 닉네임
     * @Required: Authorization (Valid JWT Token)
     * @Todo: [!] 데이터베이스 서버가 한글을 지원하는지 확인
     */
    @PostMapping("/members/{memberId}")
    public ResponseDto modifyMemberNickname(@PathVariable("memberId") String memberId, @RequestParam("name") String nickname){

        Map<String,String> resultMap  = memberService.modifyMemberNickname(memberId, nickname);
        if (resultMap.get("result").equals("fail"))
            return new ResponseDto(Status.BAD_REQUEST.getStatusCode(), resultMap.get("message") ,null);

        return new ResponseDto(Status.OK.getStatusCode(), resultMap.get("message") ,memberId);
    }


}
