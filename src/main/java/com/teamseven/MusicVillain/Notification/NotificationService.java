package com.teamseven.MusicVillain.Notification;

import com.teamseven.MusicVillain.Dto.Converter.DtoConverter;
import com.teamseven.MusicVillain.Dto.Converter.NotificationDtoConverter;
import com.teamseven.MusicVillain.Dto.NotificationDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private DtoConverter<Notification, NotificationDto> dtoConverter = new NotificationDtoConverter();
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

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
        List<Notification> notifications = notificationRepository.findByOwnerMemberId(memberId);

        if (notifications == null || notifications.isEmpty())
            return ServiceResult.of(ServiceResult.SUCCESS, "There are no notifications");

        List<NotificationDto> notificationDto = dtoConverter.convertToDtoList(notifications);
        return ServiceResult.success(notificationDto);
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
        Notification tmpNotification = notificationRepository.findByNotificationId(notificationId);
        if (tmpNotification == null) return ServiceResult.fail("Notification not found");
        if (tmpNotification.ownerRead.equals(Notification.NOTIFICATION_READ))
            return ServiceResult.of(ServiceResult.SUCCESS, "Notification already read");

        tmpNotification.ownerRead = Notification.NOTIFICATION_READ;
        notificationRepository.save(tmpNotification);
        return ServiceResult.success("Notification read successfully");
    }
}
