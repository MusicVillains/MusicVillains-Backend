package com.teamseven.MusicVillain.Feed;

import lombok.Builder;
import lombok.Data;

@Data
public class RecordResponseDto {
    public int statusCode;
    public String message;
    public String recordId;
    public String recordFileType;
    public int recordFileSize;
    public int recordDuration;
    public byte[] recordRawData;

    @Builder
    public RecordResponseDto(int statusCode, String message, String recordId, String recordFileType, int recordFileSize, int recordDuration, byte[] recordRawData){
        this.statusCode = statusCode;
        this.message = message;
        this.recordId = recordId;
        this.recordFileType = recordFileType;
        this.recordFileSize = recordFileSize;
        this.recordDuration = recordDuration;
        this.recordRawData = recordRawData;
    }
}
