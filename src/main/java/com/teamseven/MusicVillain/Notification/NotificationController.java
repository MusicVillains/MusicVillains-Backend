package com.teamseven.MusicVillain.Notification;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;
    private final MemberJwtAuthorizationManager memberAuthManager;
    private final NotificationRepository notificationRepository;


    /**
     * 멤버별 알림 조회 | GET | /notifications?memberId=memberId
     * @apiNote 멤버 별로 모든 알림을 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see NotificationService#getNotificaitonsByOwnerMemberID(String)
     *
     * @param memberId 멤버 아이디
     * @return [성공] 알림 리스트 반환,
     *         [실패] 실패 메시지 반환
     */
    @GetMapping("/notifications")
    @Operation(summary = "멤버별 알림 조회", description = "멤버 별로 모든 알림을 조회합니다.")
    // url : /notifications?memberId=memberId
    public ResponseObject getNotificationsByMemberId(@RequestParam("memberId") String memberId,
                                                     @RequestHeader HttpHeaders headers){

        /* For Debug */
        String authorization = "";
        if(headers.get("Authorization") != null) {authorization = headers.get("Authorization").get(0);}

        log.debug("getNotificationsByMemberId(..) called - @GetMapping(\"/notifications\")\n" +
                        "\t* params\n"
                        + "\t\t- memberId:\n" +
                        "\t\t\t{}\n"
                        + "\t\t- authorization:\n" +
                        "\t\t\t{}\n"
                , memberId, authorization);

        AuthorizationResult authResult = memberAuthManager.authorize(headers, memberId);

        if(authResult.isFailed()){
            log.warn("Authorization failed - {}", authResult.getMessage());
            return ResponseObject.of(Status.UNAUTHORIZED, authResult.getMessage());
        }
        log.debug("Authorization succeeded - {}", authResult.getMessage());

        ServiceResult serviceResult = notificationService.getNotificaitonsByOwnerMemberID(memberId);
        log.debug("serviceResult : {}", serviceResult);

        ResponseObject responseObject;

        if(serviceResult.isFailed()){
            log.debug("GET /notifications Failed");
            responseObject = ResponseObject.BAD_REQUEST(serviceResult.getMessage());
            log.debug("Return ResponseObject: {}", responseObject );

        } else {
            log.debug("GET /notifications Success");
            responseObject = ResponseObject.OK(serviceResult.getData());
            log.debug("Return ResponseObject: {}", responseObject );
        }

        return responseObject;
    }

    /**
     * 알림 읽음 처리 | POST | /notifications/read?notificationId=notificationId
     * @apiNote 알림을 읽음 처리합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see NotificationService#readNotification(String)
     *
     * @param notificationId 알림 아이디
     * @return [성공] 성공 메시지 반환,
     *         [실패] 실패 메시지 반환
     */
    @PostMapping("/notifications/read")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    public ResponseObject readNotification(
            @RequestParam("notificationId") String notificationId,
            @RequestHeader HttpHeaders headers){
        /* TODO: Refactoring later */
        if(notificationId == null) return ResponseObject.BAD_REQUEST("notificationId is null");

        Notification notification = notificationRepository.findByNotificationId(notificationId);
        if(notification == null) return ResponseObject.BAD_REQUEST("notification not found");
        AuthorizationResult authResult = memberAuthManager.authorize(headers, notification.getOwner().getMemberId());

        if(authResult.isFailed()){
            return ResponseObject.of(Status.UNAUTHORIZED, authResult.getMessage());
        }

        ServiceResult serviceResult = notificationService.readNotification(notificationId);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST(serviceResult.getMessage())
                : ResponseObject.OK(serviceResult.getData());
    }
}