package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Record.Record;
import com.teamseven.MusicVillain.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "feed_table")

public class Feed {

    @Id
    @Column(name = "feed_id")
    public String feedId; // PK, uuid.toString()

    @Column(name = "feed_name")
    public String feedName;

    @ManyToOne // N(Feed):1(Member) 관계
    @JoinColumn(name = "owner_id", referencedColumnName = "member_id", nullable = false)
    public Member owner;

    @OneToOne // 1(Feed):1(Record) 관계
    @JoinColumn(name = "record_id", referencedColumnName = "record_id", nullable = false)
    public Record record;

    @Column(name = "interaction_count")
    public int interactionCount;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;


    @Builder
    public Feed(String feedId, String feedName, Member owner, Record record, int interactionCount, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.feedId = feedId;
        this.feedName = feedName;
        this.owner = owner;
        this.record = record;
        this.interactionCount = interactionCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
