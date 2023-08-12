package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamseven.MusicVillain.Notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class NotificationDto {
    public String notificationId;
    public String ownerId;
    public String interactionId;
    public String ownerRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public String createdAt;

    public NotificationDto(Notification notification){
        this.setNotificationId(notification.getNotificationId());
        this.setOwnerId(notification.getOwner().getMemberId());
        this.setInteractionId(notification.getInteraction().getInteractionId());
        this.setOwnerRead(notification.getOwnerRead());
        this.setCreatedAt(notification.getCreatedAt().toString());
    }

    public static NotificationDto toDto(Notification notification){
        return new NotificationDto(notification);
    }

    public static List<NotificationDto> toDtoList(List<Notification> notificationList){
        List<NotificationDto> notificationDtoList = new ArrayList<>();
        for(Notification notification : notificationList){
            notificationDtoList.add(new NotificationDto(notification));
        }
        return notificationDtoList;
    }

}
