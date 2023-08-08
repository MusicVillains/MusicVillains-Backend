package com.teamseven.MusicVillain.Record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordService {
    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository){
        this.recordRepository = recordRepository;
    }

}
