package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamseven.MusicVillain.Notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto implements DataTransferObject<Notification, NotificationDto> {
    public String notificationId;
    public String ownerId;
    public String ownerName;
    public String musicName;
    public String musicianName;
    public String interactionId;
    public int interactionCount;
    public String ownerRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createdAt;

    public NotificationDto(Notification notification){
        // notificationFeedMusicName
        this.setMusicName(notification.getInteraction().getInteractionFeed().getMusicianName());
        this.setMusicianName(notification.getInteraction().getInteractionFeed().getMusicianName());
        this.setInteractionCount(0); // WARN: interactionCount must be calculated
        this.setNotificationId(notification.getNotificationId());
        this.setOwnerId(notification.getOwner().getMemberId());
        this.setInteractionId(notification.getInteraction().getInteractionId());
        this.setOwnerRead(notification.getOwnerRead());
        this.setCreatedAt(notification.getCreatedAt());
    }

    public NotificationDto toDto(Notification notification){
        return new NotificationDto(notification);
    }

    public List<NotificationDto> toDtoList(List<Notification> notificationList){
        List<NotificationDto> notificationDtoList = new ArrayList<>();
        for(Notification notification : notificationList){
            notificationDtoList.add(new NotificationDto(notification));
        }
        return notificationDtoList;
    }

}
