package com.teamseven.MusicVillain.Member;

import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Dto.RequestBody.WithdrawalCreationRequestBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.RequestBody.MemberCreationRequestBody;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Security.OAuth.OAuthService;
import com.teamseven.MusicVillain.Withdrawal.WithdrawalService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 관련 API")
public class MemberController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final MemberService memberService;
    private final MemberJwtAuthorizationManager authManager;
    private final WithdrawalService withdrawalService;
    private final OAuthService oAuthService;


    /**
     * 모든 회원 조회 | GET | /members
     * @apiNote 데이터 베이스에 등록된 모든 회원 정보를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see MemberService#getAllMembers()
     *
     * @return 회원 정보 리스트 반환
     */
    @GetMapping("/members")
    @Operation(summary = "모든 회원 조회", description = "데이터 베이스에 등록된 모든 회원 정보를 조회합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)))
            })
    public ResponseObject members(){
        log.debug("members() called - @GetMapping(\"/members\")");
        ServiceResult serviceResult = memberService.getAllMembers();

        return  serviceResult.isSuccessful()
                ? ResponseObject.OK(serviceResult.getData())
                : ResponseObject.BAD_REQUEST();

    }

    /**
     * 특정 회원 조회 | GET | /members/{memberId}
     * @apiNote 특정 회원의 정보를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see MemberService#getMemberById(String)
     *
     * @param memberId 조회할 회원의 멤버 아이디
     * @return [성공] 회원 정보 반환,
     *         [실패] 실패 메시지 반환
     */
    @GetMapping("/members/{memberId}")
    @Operation(summary = "특정 회원 정보 조회", description = "특정 회원의 정보를 조회합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))),
                    @ApiResponse(
                            responseCode = "400", description = "실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)))
            })
    public ResponseObject getMemberById(
            @Parameter(description = "조회할 회원의 멤버 아이디", required = true)
            @PathVariable("memberId") String memberId){

        log.debug("getMemberById() called - @GetMapping(\"/members/{memberId}\")");
        log.debug("@PathVariable(\"memberId\"): {}", memberId);
        ServiceResult serviceResult = memberService.getMemberById(memberId);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST()
                : ResponseObject.OK(serviceResult.getData());
    }

    @Hidden
    @PostMapping("/members")
    public ResponseObject insertMember(@RequestBody MemberCreationRequestBody reqbody){
        log.debug("insertMember() called - @PostMapping(\"/members\")");
        ServiceResult result = memberService.insertMember
                (reqbody.userId, reqbody.userInfo, reqbody.name, reqbody.email);
        return result.isFailed() ? ResponseObject.CREATION_FAIL(result.getData())
                : ResponseObject.CREATED(result.getData());
    }

    /**
     * 회원 이름 변경 | POST | /members/{memberId}?name={nickname}
     * @apiNote 회원의 이름(닉네임)을 변경합니다.
     * @apiNote required: Authorization (Valid JWT Token)
     * @apiNote TODO [!] 데이터베이스 필드들이 UTF-8 인코딩으로 설정되어 있는지 확인
     *
     * @author Woody K
     * @since JDK 17
     * @see MemberService#modifyMemberNickname(String, String)
     *
     * @param memberId 수정할 회원의 멤버 아이디
     * @param nickname 변경할 닉네임
     * @param requestHeader JWT Token
     * @return [성공] 성공 메시지 반환,
     *         [실패] 실패 메시지 반환
     */
    @PostMapping("/members/{memberId}")
    @Operation(summary = "회원 이름 변경", description = "회원의 이름(닉네임)을 변경합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))),
                    @ApiResponse(
                            responseCode = "400", description = "실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class)))
            })
    public ResponseObject modifyMemberNickname(
            @Parameter(description = "변경할 회원의 멤버 아이디", required = true)
            @PathVariable("memberId") String memberId,
            @Parameter(description = "변경할 닉네임", required = true)
            @RequestParam("name") String nickname,
            @Parameter(description = "JWT Token", required = true)
            @RequestHeader HttpHeaders requestHeader) {

        AuthorizationResult authResult =
                authManager.authorize(requestHeader, memberId);

        if (authResult.isFailed()) return ResponseObject.UNAUTHORIZED(authResult.getMessage());

        ServiceResult result = memberService.modifyMemberNickname(memberId, nickname);

        return result.isFailed() ? ResponseObject.BAD_REQUEST(result.getData())
                : ResponseObject.OK(result.getData());
    }

    /**
     * 회원 탈퇴 | DELETE | /members/{memberId}
     * @apiNote 회원을 탈퇴시킵니다.
     * @apiNote required: Authorization (Valid JWT Token)
     *
     * @author Woody K
     * @since JDK 17
     * @see MemberService#deleteMemberByMemberId(String)
     *
     * @param memberId 삭제할 회원의 멤버 아이디
     * @return [성공] 삭제된 멤버의 memberId 반환,
     *         [실패] 실패 메시지 반환
     */
    /* TODO: Refactioring */
    @DeleteMapping("/members/{memberId}")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))),
                    @ApiResponse(
                            responseCode = "400", description = "실패",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceResult.class)))
            })
    public ResponseObject deleteMemberByMemberId(
            @Parameter(description = "탈퇴할 회원의 멤버 아이디", required = true)
            @PathVariable("memberId") String memberId,
            @RequestBody WithdrawalCreationRequestBody withdrawalCreationRequestBody,
            @Parameter(description = "JWT Token", required = true)
            @RequestHeader HttpHeaders requestHeader){

        AuthorizationResult auth = authManager.authorize(requestHeader, memberId);
        if(auth.isFailed()){
            log.trace("Authorization failed: {}", auth.getMessage());
            return ResponseObject.onlyData(Status.UNAUTHORIZED, auth.getMessage());
        }

        ServiceResult deleteMemberResult = memberService.deleteMemberByMemberId(memberId);
        if(deleteMemberResult.isFailed()){
            log.trace("Delete Member failed: {}", deleteMemberResult.getMessage());

            return ResponseObject.onlyData(Status.BAD_REQUEST, deleteMemberResult.getMessage());
        }
        else{
            ServiceResult unlinkMemberResult = oAuthService.unlinkMember(memberId);
            if (unlinkMemberResult.isFailed()){
                log.trace("Unlink Member failed: {}", unlinkMemberResult.getMessage());
                return ResponseObject.onlyData(Status.BAD_REQUEST, "Unlink failed");
            }
        }

        ServiceResult insertWithdrawalResult = withdrawalService.insertWithdrawal(withdrawalCreationRequestBody);

        return ResponseObject.of(Status.OK, deleteMemberResult.getData());

    }

}
