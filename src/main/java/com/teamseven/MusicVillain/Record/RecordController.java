package com.teamseven.MusicVillain.Record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class RecordController {
    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService){
        this.recordService = recordService;
    }
}
