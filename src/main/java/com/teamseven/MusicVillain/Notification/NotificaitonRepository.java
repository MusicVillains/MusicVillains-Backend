package com.teamseven.MusicVillain.Notification;
import com.teamseven.MusicVillain.Interaction.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaitonRepository extends JpaRepository<Notification, String> {

    List<Notification> findAll();;
    Notification findByNotificationId(String notificationId);

    void deleteByInteractionInteractionId(String interactionId);

    void deleteByInteraction(Interaction i);

    List<Notification> findByOwnerMemberId(String memberId);
}
