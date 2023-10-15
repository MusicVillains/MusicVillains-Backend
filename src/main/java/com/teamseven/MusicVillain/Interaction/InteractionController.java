package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Dto.RequestBody.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "인터렉션 관련 API")
public class InteractionController {
    
    private final InteractionService interactionService;

    private MemberJwtAuthorizationManager memberAuthManager;

    public InteractionController(InteractionService interactionService,
                                 MemberJwtAuthorizationManager memberJwtAuthorizationManager) {
        this.interactionService = interactionService;
        this.memberAuthManager = memberJwtAuthorizationManager;
    }

    /**
     * 상호작용 생성 | POST | /interactions
     * @apiNote 상호작용을 생성합니다. (좋아요, 좋아요 취소)
     *
     * @author Woody K
     * @since JDK 17
     * @see InteractionService#insertInteraction(InteractionCreationRequestBody)
     *
     * @param requestBody 상호작용 생성 요청 바디
     * @return [성공] 좋아요:상호작용 생성 메시지+상호작용 아이디, 취소:상호작용 삭제 메시지 반환
     *         [실패] 실패 메시지 반환
     */
    @PostMapping("/interactions")
    @Operation(summary = "상호작용 생성", description = "상호작용을 생성합니다.")
    public ResponseObject doInteraction(
            @RequestBody InteractionCreationRequestBody requestBody,
            @RequestHeader HttpHeaders headers
    ){

        AuthorizationResult authResult
                = memberAuthManager.authorize(headers,requestBody.getMemberId());

        if(authResult.isFailed()){
            return ResponseObject.UNAUTHORIZED(authResult.getMessage());
        }

        ServiceResult result =  interactionService.insertInteraction(requestBody);
        return result.isFailed() ? ResponseObject.BAD_REQUEST(result.getMessage())
                : ResponseObject.OK(result.getMessage() + " - " + result.getData());
    }

    /**
     * 특정 피드의 좋아요 수 조회 | GET | /interactions/count?feedId={feedId}
     * @apiNote 특정 피드의 좋아요 수를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see InteractionService#getInteractionCountByFeedId(String)
     *
     * @param feedId 조회할 피드 아이디
     * @return [성공] 상호작용 수 반환
     *         [실패] 실패 메시지 반환
     */
    @GetMapping("/interactions/count")
    @Operation(summary = "상호작용 수 조회", description = "상호작용 수를 조회합니다.")
    // url: /interaction/count?feedId={feedId}
    public ResponseObject getInteractionCountByFeedId(@RequestParam("feedId") String feedId){

        ServiceResult result = interactionService.getInteractionCountByFeedId(feedId);

        return result.isFailed() ? ResponseObject.BAD_REQUEST(result.getData())
                : ResponseObject.OK(result.getData());
    }

}
