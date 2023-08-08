package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.ResponseDto;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.teamseven.MusicVillain.ResponseDto;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class FeedController {
    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService){
        this.feedService = feedService;
    }

    @PostMapping("/feeds")
    // feed 생성
    public ResponseDto createFeed(@RequestBody FeedCreationRequestBody reqBody){
        Map resultMap=
        feedService.insertFeed(reqBody.getOwnerId(),
               reqBody.getFeedName(),
               reqBody.getRecordName(),
               reqBody.getRecordFileType(),
               reqBody.getRecordDuration(), reqBody.getRecordRawData());

        if (resultMap.get("result").equals("fail")) return ResponseDto.builder()
                .statusCode(Status.CREATION_FAIL.getStatusCode())
                .message("Feed creation failed")
                .data(null)
                .build();

        return ResponseDto.
                builder()
                .statusCode(Status.OK.getStatusCode())
                .message("Feed created successfully")
                .data(resultMap.get("feedId").toString())
                .build();
    }
}
