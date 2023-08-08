package com.teamseven.MusicVillain.Record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "record_table")
public class Record {
    @Id
    @Column(name = "record_id")
    public String recordId;

    @Column(name = "record_name")
    public String recordName;

    @Column(name = "record_filesize")
    public int recordFileSize;

    @Column(name = "record_duration")
    public int recordDuration;

    @Column(name = "record_rawdata")
    public byte[] recordRawData;


}
