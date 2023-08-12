package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.RequestBody.MemberCreationRequestBody;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemberController {
    private final MemberService memberService;
    private final MemberJwtAuthorizationManager authManager;

    @Autowired
    public MemberController(MemberService memberService, MemberJwtAuthorizationManager authManager){
        this.memberService = memberService;
        this.authManager = authManager;
    }

    @GetMapping("/members")
    public ResponseObject members(){
        List<Member> memberList = memberService.getAllMembers();
        return ResponseObject.of(Status.OK, memberList);
    }
    @GetMapping("/members/{memberId}")
    public ResponseObject getMemberById(@PathVariable("memberId") String memberId){
        ServiceResult serviceResult = memberService.getMemberById(memberId);
        return serviceResult.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, null)
                : ResponseObject.of(Status.OK, serviceResult.getData());
    }

    @PostMapping("/members")
    public ResponseObject insertMember(@RequestBody MemberCreationRequestBody memberCreationRequestBody){
        ServiceResult result = memberService.insertMember(memberCreationRequestBody);

        return result.isFailed() ? ResponseObject.of(Status.CREATION_FAIL, result.getData())
                : ResponseObject.of(Status.CREATED, result.getData());
    }

    /**
     * @Title: modifyMemberNickname
     * @Author: Woody K
     * @Date: 2023/08/09
     * @Description: 회원 정보 수정
     *
     * @Param memberId : 수정할 회원의 멤버 아이디
     * @Param nickname : 수정할 닉네임
     * @Required: Authorization (Valid JWT Token)
     * @Todo: [!] 데이터베이스 필드들이 UTF-8 인코딩으로 설정되어 있는지 확인
     */
    @PostMapping("/members/{memberId}")
    public ResponseObject modifyMemberNickname(@PathVariable("memberId") String memberId, @RequestParam("name") String nickname,
                                               @RequestHeader HttpHeaders requestHeader) {

        AuthorizationResult authResult =
                authManager.authorize(requestHeader, memberId);

        if (authResult.isFailed()) return ResponseObject.of(Status.UNAUTHORIZED, authResult.getMessage());

        ServiceResult result = memberService.modifyMemberNickname(memberId, nickname);

        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getData())
                : ResponseObject.of(Status.OK, result.getData());
    }

    /**
     * @Title: deleteMemberByMemberId
     * @Author: Woody K
     * @Date: 2023/08/09
     * @Param memberId : 삭제할 회원의 멤버 아이디
     * @Required Authorization (Valid JWT Token)
     * @Return 성공시, 삭제된 멤버의 memberId 반환
     * @Return 실패시, 실패 메시지 반환
     */
    @DeleteMapping("/members/{memberId}")
    public ResponseObject deleteMemberByMemberId(
            @PathVariable("memberId") String memberId,
            @RequestHeader HttpHeaders requestHeader){

        authManager.authorize(requestHeader, memberId);

        ServiceResult result = memberService.deleteMemberByMemberId(memberId);

        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getMessage())
                : ResponseObject.of(Status.OK, result.getData());
    }

}
