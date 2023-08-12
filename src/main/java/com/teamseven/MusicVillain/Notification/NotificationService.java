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

    public ServiceResult getNotificaitonsByOwnerMemberID(String memberId){
        Member tmpMember =memberRepository.findByMemberId(memberId);
        if (tmpMember == null) return ServiceResult.fail("Member not found");
        List<Notification> notifications = notificaitonRepository.findByOwnerMemberId(memberId);
        return ServiceResult.success( NotificationDto.toDtoList(notifications));
    }

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
