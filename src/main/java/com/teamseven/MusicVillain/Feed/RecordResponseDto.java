package com.teamseven.MusicVillain.Feed;

import lombok.Data;

@Data
public class RecordResponseDto {
    public int statusCode;
    public String message;
    public String recordId;
    public byte[] recordRawData;
}
