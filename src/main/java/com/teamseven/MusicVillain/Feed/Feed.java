package com.teamseven.MusicVillain.Feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamseven.MusicVillain.Record.Record;
import com.teamseven.MusicVillain.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "feed_table")

public class Feed {

    @Id
    @Column(name = "feed_id")
    public String feedId; // PK, uuid.toString()

    @Column(name = "feed_name")
    public String feedName;

    @Column(name = "feed_type")
    public String feedType;

    @ManyToOne // N(Feed):1(Member) 관계
    @JoinColumn(name = "owner_id", referencedColumnName = "member_id", nullable = false)
    public Member owner;

    @OneToOne // 1(Feed):1(Record) 관계
    @JoinColumn(name = "record_id", referencedColumnName = "record_id", nullable = false)
    public Record record;

    @Column(name = "interaction_count")
    public int interactionCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name ="description")
    public String description;

    @Builder
    public Feed(String feedId, String feedName, String feedType, Member owner, Record record, int interactionCount, LocalDateTime createdAt, LocalDateTime updatedAt, String description) {
        this.feedId = feedId;
        this.feedName = feedName;
        this.feedType = feedType;
        this.owner = owner;
        this.record = record;
        this.interactionCount = interactionCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }
}
