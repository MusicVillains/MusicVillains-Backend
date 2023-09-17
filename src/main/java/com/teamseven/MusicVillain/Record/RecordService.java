package com.teamseven.MusicVillain.Record;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;

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
        if (recordRepository.findByRecordId(recordId) == null) {
            throw new IllegalArgumentException("Record Not Found.");
        }
        recordRepository.deleteByRecordId(recordId);
    }
}
