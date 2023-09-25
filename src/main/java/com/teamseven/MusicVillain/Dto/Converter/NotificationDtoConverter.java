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
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .ownerId(notification.getOwner().getMemberId())
                .ownerName(notification.getOwner().getName())
                .interactionId(notification.getInteraction().getInteractionId())
                .musicName(notification.getInteraction().getInteractionFeed().getMusicName())
                .musicianName(notification.getInteraction().getInteractionFeed().getMusicianName())
                .interactionCount(notification.getInteraction().getInteractionFeed().getInteractionList().size())
                .ownerRead(notification.getOwnerRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    @Override
    public List<NotificationDto> convertToDtoList(List<Notification> notifications) {
        if(notifications.isEmpty()) return null;

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
