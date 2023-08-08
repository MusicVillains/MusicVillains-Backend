package com.teamseven.MusicVillain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "owner_id")
    public String ownerId;

    @Column(name = "record_id")
    public String recordId;

    @Column(name = "interaction_count")
    public int interactionCount;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;


}
