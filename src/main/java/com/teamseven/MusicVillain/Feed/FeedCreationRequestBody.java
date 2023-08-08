package com.teamseven.MusicVillain.Feed;

import lombok.Data;

@Data
public class FeedCreationRequestBody {
    public String ownerId; // 피드 생성한 사람 memberId
    public String feedName; // 생성할 피드명
    public int recordDuration;
    public byte[] recordRawData;
}
