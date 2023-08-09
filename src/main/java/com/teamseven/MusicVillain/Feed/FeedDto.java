package com.teamseven.MusicVillain.Feed;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedDto {

    public String feedId;
    public String feedName;
    public String feedType;
    public String ownerId;
    public String ownerName;
    public String recordId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    public String description;

    public FeedDto(Feed feed){
        this.setFeedId(feed.getFeedId());
        this.setFeedName(feed.getFeedName());
        this.setFeedType(feed.getFeedType());
        this.setOwnerId(feed.getOwner().getMemberId());
        this.setOwnerName(feed.getOwner().getName());
        this.setRecordId(feed.getRecord().getRecordId());
        this.setCreatedAt(feed.getCreatedAt());
        this.setUpdatedAt(feed.getUpdatedAt());
        this.setDescription(feed.getDescription());
    }
    public static FeedDto toFeedDto(Feed feed){
        return new FeedDto(feed);
    }

    public static List<FeedDto> toFeedDtoList(List<Feed> feedList){
        List<FeedDto> feedDtoList = new ArrayList<>();
        for(Feed feed : feedList){
            feedDtoList.add(new FeedDto(feed));
        }
        return feedDtoList;
    }
}
