package com.teamseven.MusicVillain.Record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class RecordService {
    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository){
        this.recordRepository = recordRepository;
    }

    public List<Record> getAllRecords(){
        return recordRepository.findAll();
    }

    public void DeleteRecordByRecordId(String recordId){
        recordRepository.deleteByRecordId(recordId);
    }
}
