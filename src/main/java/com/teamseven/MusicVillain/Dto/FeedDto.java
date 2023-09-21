package com.teamseven.MusicVillain.Dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamseven.MusicVillain.Feed.Feed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedDto implements DataTransferObject<Feed, FeedDto>{

    public String feedId;
    public String feedType;
    public String ownerId;
    public String ownerName;
    public String recordId;
    public String musicName;
    public String musicianName;
    public int viewCount;
    public int interactionCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    public String description;

    public FeedDto(Feed feed){
        this.setFeedId(feed.getFeedId());
        this.setFeedType(feed.getFeedType());
        this.setOwnerId(feed.getOwner().getMemberId());
        this.setOwnerName(feed.getOwner().getName());
        this.setRecordId(feed.getRecord().getRecordId());
        this.setCreatedAt(feed.getCreatedAt());
        this.setUpdatedAt(feed.getUpdatedAt());
        this.setDescription(feed.getDescription());
        this.setMusicName(feed.getMusicName());
        this.setMusicianName(feed.getMusicianName());
        this.setViewCount(feed.getViewCount());
    }
    @Override
    public FeedDto toDto(Feed feed) {
        return new FeedDto(feed);
    }

    @Override
    public List<FeedDto> toDtoList(List<Feed> feedList) {
        List<FeedDto> feedDtoList = new ArrayList<>();
        for (Feed feed : feedList) {
            feedDtoList.add(new FeedDto(feed));
        }
        return feedDtoList;
    }
}
