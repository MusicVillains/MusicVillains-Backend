package com.teamseven.MusicVillain.Feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Record.Record;
import com.teamseven.MusicVillain.Member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "feed_table")

public class Feed {

    @Id
    @Column(name = "feed_id")
    public String feedId; // PK, uuid.toString()

    @Column(name = "feed_type")
    public String feedType;

    @ManyToOne(fetch = FetchType.LAZY)  // N(Feed):1(Member) 관계
    @JoinColumn(name = "owner_id", referencedColumnName = "member_id")
    public Member owner;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY) // 1(Feed):1(Record) 관계
    @JoinColumn(name = "record_id", referencedColumnName = "record_id")
    public Record record;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name ="description")
    public String description;

    @Column(name="view_count")
    public int viewCount;

    @Column(name="music_name")
    public String musicName;

    @Column(name="musician_name")
    public String musicianName;

    @OneToMany(mappedBy = "interactionFeed")
    List<Interaction> interactionList;
    @Builder
    public Feed(String feedId, String feedType, Member owner, Record record, LocalDateTime createdAt, LocalDateTime updatedAt, String description, int viewCount, String musicName, String musicianName) {
        this.feedId = feedId;
        this.feedType = feedType;
        this.owner = owner;
        this.record = record;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.viewCount = viewCount;
        this.musicName = musicName;
        this.musicianName = musicianName;
    }
}
