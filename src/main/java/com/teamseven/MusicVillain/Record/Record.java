package com.teamseven.MusicVillain.Record;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "record_table")
public class Record {
    @Id
    @Column(name = "record_id")
    public String recordId;


    @Column(name = "record_filetype", nullable =true)
    public String recordFileType;

    @Column(name = "record_filesize", nullable =true)
    public int recordFileSize;

    @Column(name = "record_duration", nullable =true)
    public int recordDuration;

    @Lob
    @Column(name = "record_rawdata", nullable =true)
    public byte[] recordRawData;

    @Builder
    public Record(String recordId, String recordFileType, int recordFileSize, int recordDuration, byte[] recordRawData){
        this.recordId = recordId;
        this.recordFileType = recordFileType;
        this.recordFileSize = recordFileSize;
        this.recordDuration = recordDuration;
        this.recordRawData = recordRawData;
    }

}
