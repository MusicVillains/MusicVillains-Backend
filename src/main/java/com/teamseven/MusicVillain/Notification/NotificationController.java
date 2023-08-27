package com.teamseven.MusicVillain.Notification;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Hidden
@RestController
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

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
    public ResponseObject getNotificationsByMemberId(@RequestParam("memberId") String memberId){
        ServiceResult serviceResult = notificationService.getNotificaitonsByOwnerMemberID(memberId);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST(serviceResult.getData())
                : ResponseObject.OK(serviceResult.getData());
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
    public ResponseObject readNotification(@RequestParam("notificationId") String notificationId){
        ServiceResult serviceResult = notificationService.readNotification(notificationId);
        return serviceResult.isFailed() ? ResponseObject.BAD_REQUEST(serviceResult.getMessage())
                : ResponseObject.OK(serviceResult.getData());
    }
}