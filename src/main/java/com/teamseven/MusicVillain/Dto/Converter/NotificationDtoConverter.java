package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.NotificationDto;
import com.teamseven.MusicVillain.Notification.Notification;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class NotificationDtoConverter implements DtoConverter<Notification, NotificationDto>{

    @Override
    public NotificationDto convertToDto(Notification notification) {
        NotificationDto notificationDto = new NotificationDto();

        if(notification.getNotificationId() != null)
            notificationDto.setNotificationId(notification.getNotificationId());

        if(notification.getOwner() != null) {
            notificationDto.setOwnerId(notification.getOwner().getMemberId());
            notificationDto.setOwnerName(notification.getOwner().getName());
        }

        if(notification.getInteraction() != null) {
            notificationDto.setInteractionId(notification.getInteraction().getInteractionId());

            if(notification.getInteraction().getInteractionFeed() != null) {
                notificationDto.setMusicName(notification.getInteraction().getInteractionFeed().getMusicName());
                notificationDto.setMusicianName(notification.getInteraction().getInteractionFeed().getMusicianName());
                notificationDto.setInteractionCount(notification.getInteraction().getInteractionFeed().getInteractionList().size());
            }
        }

        if(notification.getOwnerRead() != null){
            notificationDto.setOwnerRead(notification.getOwnerRead());
        }

        if(notification.getCreatedAt() != null){
            notificationDto.setCreatedAt(notification.getCreatedAt());
        }

        return notificationDto;
    }

    @Override
    public List<NotificationDto> convertToDtoList(List<Notification> notifications) {
        if(notifications.isEmpty()) return null;

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
