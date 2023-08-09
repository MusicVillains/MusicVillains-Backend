package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "interaction_table")
public class Interaction {
    @Id
    @Column(name = "interaction_id")
    public String interactionId;

    @ManyToOne(fetch = FetchType.LAZY) // N(Interaction):1(Feed) 관계, reference = feed_table.feed_id
    @JoinColumn(name = "feed_id", referencedColumnName = "feed_id")
    public Feed interactionFeed;

    @OneToOne(fetch = FetchType.LAZY) // N(Interaction):1(Member) 관계,
    // interaction_table.member_id 필드의 reference는 member_table.member_id
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    public Member interactionMember;

    @Builder
    public Interaction(String interactionId, Feed interactionFeed, Member interactionMember) {
        this.interactionId = interactionId;
        this.interactionFeed = interactionFeed;
        this.interactionMember = interactionMember;
    }
}
