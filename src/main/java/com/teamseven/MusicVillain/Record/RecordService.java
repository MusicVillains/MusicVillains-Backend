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

    /**
     * 모든 Record 객체를 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @return Record 객체 리스트
     */
    public List<Record> getAllRecords(){
        return recordRepository.findAll();
    }

    /**
     * 특정 Record를 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param recordId 삭제할 Record의 아이디
     */
    public void DeleteRecordByRecordId(String recordId){
        recordRepository.deleteByRecordId(recordId);
    }
}
