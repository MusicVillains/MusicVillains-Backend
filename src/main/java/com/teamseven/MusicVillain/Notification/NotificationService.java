package com.teamseven.MusicVillain.Notification;

import com.teamseven.MusicVillain.Dto.NotificationDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificaitonRepository notificaitonRepository;

    @Autowired
    public NotificationService(NotificaitonRepository notificaitonRepository,
                               MemberRepository memberRepository){
        this.notificaitonRepository = notificaitonRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 특정 회원의 모든 알림을 가져옵니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 알림을 조회할 회원의 ID
     * @return ServiceResult 객체. 성공 또는 실패 메시지를 포함.
     */
    public ServiceResult getNotificaitonsByOwnerMemberID(String memberId){
        Member tmpMember =memberRepository.findByMemberId(memberId);
        if (tmpMember == null) return ServiceResult.fail("Member not found");
        List<Notification> notifications = notificaitonRepository.findByOwnerMemberId(memberId);
        return ServiceResult.success( NotificationDto.toDtoList(notifications));
    }

    /**
     * 특정 알림을 읽음 상태로 변경합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param notificationId 읽음 상태로 변경할 알림의 ID
     * @return ServiceResult 객체. 성공 또는 실패 메시지를 포함.
     */
    public ServiceResult readNotification(String notificationId){
        Notification tmpNotification = notificaitonRepository.findByNotificationId(notificationId);
        if (tmpNotification == null) return ServiceResult.fail("Notification not found");
        if (tmpNotification.ownerRead.equals(Notification.NOTIFICATION_READ))
            return ServiceResult.fail("Notification already read");

        tmpNotification.ownerRead = Notification.NOTIFICATION_READ;
        notificaitonRepository.save(tmpNotification);
        return ServiceResult.success("Notification read successfully");
    }
}
