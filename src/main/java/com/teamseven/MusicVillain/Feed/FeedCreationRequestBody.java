package com.teamseven.MusicVillain.Feed;

import lombok.Data;

@Data
public class FeedCreationRequestBody {
    public String ownerId; // 피드 생성한 사람 memberId
    public String feedName; // 생성할 피드명 (확장자 반드시 포함)
    public String recordName; // 레코드이름 -> 자동생성해도 될듯?
    public int recordDuration;
    public String recordFileType;
    public byte[] recordRawData;
}
