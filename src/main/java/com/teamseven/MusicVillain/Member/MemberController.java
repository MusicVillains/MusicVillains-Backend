package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.RequestBody.MemberCreationRequestBody;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "회원 관련 API")
public class MemberController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;
    private final MemberJwtAuthorizationManager authManager;

    @Autowired

    public MemberController(MemberService memberService, MemberJwtAuthorizationManager authManager){
        this.memberService = memberService;
        this.authManager = authManager;
    }
//    @Tag(name = "회원 관련 API", description = "Swagger 테스트")
    @GetMapping("/members")
    @Operation(summary = "모든 회원 조회", description = "데이터 베이스에 등록된 모든 회원 정보를 조회합니다.")
    public ResponseObject members(){
        log.debug("members() called - @GetMapping(\"/members\")");
        List<Member> memberList = memberService.getAllMembers();
        return ResponseObject.of(Status.OK, memberList);
    }
    @GetMapping("/members/{memberId}")
    @Operation(summary = "특정 회원 정보 조회", description = "특정 회원의 정보를 조회합니다.")
    public ResponseObject getMemberById(
            @Parameter(description = "조회할 회원의 멤버 아이디", required = true)
            @PathVariable("memberId") String memberId){
        log.debug("getMemberById() called - @GetMapping(\"/members/{memberId}\")");
        log.debug("@PathVariable(\"memberId\"): {}", memberId);
        ServiceResult serviceResult = memberService.getMemberById(memberId);
        return serviceResult.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, null)
                : ResponseObject.of(Status.OK, serviceResult.getData());
    }

    @Hidden
    @PostMapping("/members")
    public ResponseObject insertMember(@RequestBody MemberCreationRequestBody memberCreationRequestBody){
        log.debug("insertMember() called - @PostMapping(\"/members\")");
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
    @Operation(summary = "회원 이름 변경", description = "회원의 이름(닉네임)을 변경합니다.")

    public ResponseObject modifyMemberNickname(
            @Parameter(description = "변경할 회원의 멤버 아이디", required = true)
            @PathVariable("memberId") String memberId,
            @Parameter(description = "변경할 닉네임", required = true)
            @RequestParam("name") String nickname,
            @Parameter(description = "JWT Token", required = true)
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
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다.")

    public ResponseObject deleteMemberByMemberId(
            @Parameter(description = "탈퇴할 회원의 멤버 아이디", required = true)
            @PathVariable("memberId") String memberId,
            @Parameter(description = "JWT Token", required = true)
            @RequestHeader HttpHeaders requestHeader){

        authManager.authorize(requestHeader, memberId);

        ServiceResult result = memberService.deleteMemberByMemberId(memberId);

        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getMessage())
                : ResponseObject.of(Status.OK, result.getData());
    }

}
