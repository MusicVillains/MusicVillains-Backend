package com.teamseven.MusicVillain.Notification;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject_old;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import io.swagger.v3.oas.annotations.Hidden;
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


    @GetMapping("/notifications")
    // url : /notifications?memberId=memberId
    public ResponseObject_old getNotificationsByMemberId(@RequestParam("memberId") String memberId){
        ServiceResult serviceResult = notificationService.getNotificaitonsByOwnerMemberID(memberId);

        return serviceResult.isFailed() ? ResponseObject_old.of(Status.BAD_REQUEST, serviceResult.getData())
                : ResponseObject_old.of(Status.OK, serviceResult.getData());
    }
    @PostMapping("/notifications/read")
    public ResponseObject_old readNotification(@RequestParam("notificationId") String notificationId){
        ServiceResult serviceResult = notificationService.readNotification(notificationId);
        return serviceResult.isFailed() ? ResponseObject_old.of(Status.BAD_REQUEST, serviceResult.getMessage())
                : ResponseObject_old.of(Status.OK, serviceResult.getData());
    }
}