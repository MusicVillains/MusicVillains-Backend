package com.teamseven.MusicVillain.Record;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(name = "음원 관련 API")
public class RecordController {
    private final RecordService recordService;

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
    @Operation(summary = "업로드된 모든 음원 정보를 반환합니다.")
    public List<Record> records(){
        return recordService.getAllRecords();
    }
}
