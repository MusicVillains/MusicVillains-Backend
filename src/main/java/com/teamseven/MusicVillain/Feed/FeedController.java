package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.ResponseDto;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import com.teamseven.MusicVillain.ResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FeedController {
    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService){
        this.feedService = feedService;
    }

    @GetMapping("/feeds")
    // 모든 feed 가져오기
    public List<Feed> getAllFeeds(){
        return this.feedService.getAllFeeds();
    }
    @PostMapping("/feeds2")
    // feed 생성
    public ResponseDto createFeed(@RequestBody FeedCreationRequestBody reqBody){
        Map resultMap=
        feedService.insertFeed(reqBody.getOwnerId(),
               reqBody.getFeedName(),
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
    @PostMapping("/feeds")
    public ResponseDto createFeed2(
            // MultipartFile 받으려면 @RequestParam 사용해야하므로 다음과 같이 form-data로 받도록 함.
            @RequestParam("feedName") String feedName,
            @RequestParam("ownerId") String ownerId,
            @RequestParam("description") String feedDescription,
            @RequestParam("recordDuration") int recordDuration,
            @RequestParam("recordFile") MultipartFile recordFile) throws IOException {

        Map resultMap= feedService.insertFeed2(feedName, ownerId, feedDescription, recordDuration, recordFile);

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
    @GetMapping("/feeds/record")
    // feedId로 record 가져오기
    // http://localhost:8080/feeds/record?feedId=6a9a17e91a334c3498213b6c89ac22c3
    public RecordResponseDto  getFeedRecord(@RequestParam("feedId") String feedId){
        RecordResponseDto recordResponseDto = feedService.getRecordByFeedId(feedId);
//        encodedByteArrayAsString = Base64.getEncoder().encodeToString(recordResponseDto.getRecordRawData());
//        System.out.println("encodedByteArrayAsString: " + encodedByteArrayAsString);
//        byte[] decodedByteArray = Base64.getDecoder().decode(encodedByteArrayAsString);
//        System.out.println("decodedByteArray: " + decodedByteArray);
//        String resourcePath = "/Users/gunmo/Desktop/Team7-Backend/src/main/resources/static/output." + recordResponseDto.getRecordFileType().split("/")[1];
//        writeBytesToFile(decodedByteArray, resourcePath);

        return recordResponseDto;
    }

    //for test
    public static void writeBytesToFile(byte[] data, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(data);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/feeds/member")
    // memberId로 feed 가져오기
    // 주소 예시: http://localhost:8080/feeds/member?id=1
    // 특정 멤버에 대한 피드 가져오기
    // [!] Implement Later
    public String getFeedByMemberId(@RequestParam("id") String memberId){
        //return feedService.getFeedByMemberId(memberId);
        return "";
    }

}
