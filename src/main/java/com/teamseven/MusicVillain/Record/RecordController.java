package com.teamseven.MusicVillain.Record;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class RecordController {
    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService){
        this.recordService = recordService;
    }

    /**
     * 모든 Record 객체 조회 | GET | /records
     * @apiNote 모든 Record 객체를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see RecordService#getAllRecords()
     *
     * @return Record 객체 리스트 반환
     */
    @GetMapping("/records")
    @Operation(summary = "모든 Record 객체를 반환합니다.")
    public List<Record> records(){
        return recordService.getAllRecords();
    }
}
