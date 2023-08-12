package com.teamseven.MusicVillain.Notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_table")
@Data
public class Notification {

    @Id
    @Column(name = "notification_id")
    public String notificationId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // N(Notification):1(Member) 관계
    @JoinColumn(name = "owner_id", referencedColumnName = "member_id")
    public Member owner;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY) // 1(Notification):1(Interaction) 관계
    @JoinColumn(name = "interaction_id", referencedColumnName = "interaction_id")
    public Interaction interaction;

    @Column(name = "owner_read")
    public String ownerRead;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @JsonIgnore
    public final static String NOTIFICATION_READ = "READ";
    @JsonIgnore
    public final static String NOTIFICATION_UNREAD = "UNREAD";
}
