package com.teamseven.MusicVillain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "interaction_table")
public class Interaction {
    @Id
    @Column(name = "interaction_id")
    public String interactionId;

    @Column(name = "feed_id")
    public String feedId;

    @Column(name = "member_id")
    public String memberId;
}
