package com.teamseven.MusicVillain.Entity;

import jakarta.persistence.*;
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

    @ManyToOne // N(Interaction):1(Feed) 관계, reference = feed_table.feed_id
    @JoinColumn(name = "feed_id", referencedColumnName = "feed_id", nullable = false)
    public Feed feed;

    @OneToOne // N(Interaction):1(Member) 관계,
    // interaction_table.member_id 필드의 reference는 member_table.member_id
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    public Member member;
}
