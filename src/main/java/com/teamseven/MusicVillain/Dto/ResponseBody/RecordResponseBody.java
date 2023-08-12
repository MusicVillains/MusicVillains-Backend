package com.teamseven.MusicVillain.Dto.ResponseBody;

import lombok.Builder;
import lombok.Data;

@Data
public class RecordResponseBody {
    public int statusCode;
    public String message;
    public String recordId;
    public String recordFileType; // image/png
    public int recordFileSize;
    public int recordDuration;
    public byte[] recordRawData;

    @Builder
    public RecordResponseBody(int statusCode, String message, String recordId, String recordFileType, int recordFileSize, int recordDuration, byte[] recordRawData){
        this.statusCode = statusCode;
        this.message = message;
        this.recordId = recordId;
        this.recordFileType = recordFileType;
        this.recordFileSize = recordFileSize;
        this.recordDuration = recordDuration;
        this.recordRawData = recordRawData;
    }
}
