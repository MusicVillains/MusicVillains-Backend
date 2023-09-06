package com.teamseven.MusicVillain.Withdrawal;

import com.teamseven.MusicVillain.Dto.DataTransferObject;
import com.teamseven.MusicVillain.Dto.RequestBody.WithdrawalCreationRequestBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(name = "탈퇴원 관련 API")
public class WithdrawalController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final WithdrawalService withdrawalService;

    /**
     * 모든 탈퇴원 조회 | GET | /withdrawals
     * @apiNote 데이터 베이스에 등록된 모든 탈퇴원 정보를 조회합니다.
     *
     * @author Claire J
     * @since JDK 17
     * @see WithdrawalService#getAllWithdrawals()
     *
     * @return 탈퇴원 정보 리스트 반환
     */
    @GetMapping("/withdrawals")
    @Operation(summary = "모든 탈퇴원 조회", description = "데이터 베이스에 등록된 모든 탈퇴원 정보를 조회합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json"))
            })
    public ResponseObject withdrawals(){
        log.debug("withdrawals() called - @GetMapping(\"/withdrawals\")");
        List<DataTransferObject> resultDtoList = withdrawalService.getAllWithdrawals();
        return ResponseObject.OK(resultDtoList);
    }

    /**
     * 특정 탈퇴원 조회 | GET | /withdrawals/{withdrawalId}
     * @apiNote 데이터 베이스에 등록된 특정 탈퇴원 정보를 조회합니다.
     *
     * @author Claire J
     * @since JDK 17
     * @see WithdrawalService#getWithdrawalById(String)
     *
     * @param withdrawalId 조회할 탈퇴원의 아이디
     * @return [성공] 탈퇴원 정보 반환
     *         [실패] 실패 메시지 반환
     */
    @GetMapping("/withdrawals/{withdrawalId}")
    @Operation(summary = "특정 탈퇴원 조회", description = "데이터 베이스에 등록된 특정 탈퇴원 정보를 조회합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json"))
            })
    public ResponseObject withdrawal(@PathVariable String withdrawalId){
        log.debug("withdrawal() called - @GetMapping(\"/withdrawals/{withdrawalId}\")");
        ServiceResult serviceResult = withdrawalService.getWithdrawalById(withdrawalId);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST()
                : ResponseObject.OK(serviceResult.getData());
    }

    /**
     * 탈퇴원 등록 | POST | /withdrawals
     * @apiNote 데이터 베이스에 탈퇴원 정보를 등록합니다.
     * @apiNote 탈퇴원 정보는 회원 정보와 연결됩니다.
     * @apiNote 탈퇴원 정보는 회원 탈퇴 시 자동으로 생성됩니다.
     *
     * @author Claire J
     * @since JDK 17
     * @see WithdrawalService#insertWithdrawal(WithdrawalCreationRequestBody)
     *
     * @param withdrawalCreationRequestBody 등록할 탈퇴원 정보
     * @return [성공] 탈퇴원 정보 반환
     *         [실패] 실패 메시지 반환
     */
    @PostMapping("/withdrawals")
    @Operation(summary = "탈퇴원 등록", description = "데이터 베이스에 탈퇴원 정보를 등록합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json"))
            })
    public ResponseObject saveWithdrawal(@RequestBody WithdrawalCreationRequestBody withdrawalCreationRequestBody){
        log.debug("saveWithdrawal() called - @PostMapping(\"/withdrawals\")");
        ServiceResult serviceResult = withdrawalService.insertWithdrawal(withdrawalCreationRequestBody);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST()
                : ResponseObject.OK(serviceResult.getData());
    }

    /**
     * 탈퇴원 정보 삭제 | DELETE | /withdrawals?withdrawalId={withdrawalId}
     * @apiNote 데이터 베이스에 등록된 탈퇴원 정보를 삭제합니다.
     *
     * @author Claire J
     * @since JDK 17
     * @see WithdrawalService#deleteWithdrawalById(String)
     * @param withdrawalId 삭제할 탈퇴원의 ID
     *                     @return [성공] 성공 메시지 반환
     *                     [실패] 실패 메시지 반환
     */
    @Transactional
    @DeleteMapping("/withdrawals")
    @Operation(summary = "탈퇴원 정보 삭제", description = "데이터 베이스에 등록된 탈퇴원 정보를 삭제합니다.",
            responses = {@ApiResponse(
                    responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json"))
            })
    public ResponseObject deleteWithdrawal(@RequestParam String withdrawalId){
        log.debug("deleteWithdrawal() called - @DeleteMapping(\"/withdrawals\")");
        ServiceResult serviceResult = withdrawalService.deleteWithdrawalById(withdrawalId);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST()
                : ResponseObject.OK(serviceResult.getData());
    }


}
