package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.ResponseDto;
import com.teamseven.MusicVillain.ResponseObject;
import com.teamseven.MusicVillain.ServiceResult;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
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
    public ResponseObject getAllFeeds(){
        ServiceResult result = feedService.getAllFeeds();
        return ResponseObject.of(Status.OK,result.getData());
    }
    @GetMapping("/feeds/{feedId}")
    // FeedDto
    public ResponseObject getFeedById(@RequestParam("feedId") String feedId){
        return ResponseObject.of(Status.OK,feedService.getFeedByFeedId(feedId));
    }

    @PostMapping("/feeds")
    public ResponseObject createFeed(
            // MultipartFile 받으려면 @RequestParam 사용해야하므로 다음과 같이 form-data로 받도록 함.
            @RequestParam("feedName") String feedName,
            @RequestParam("feedType") String feedType,
            @RequestParam("ownerId") String ownerId,
            @RequestParam("description") String feedDescription,
            @RequestParam("recordDuration") int recordDuration,
            @RequestParam("recordFile") MultipartFile recordFile) throws IOException {

        ServiceResult result = feedService.insertFeed(feedName, ownerId, feedType,feedDescription, recordDuration, recordFile);
        if (result.isFailed()) return ResponseObject.of(Status.CREATION_FAIL, null);

        return ResponseObject.of(Status.OK, result.getData());
    }
    @GetMapping("/feeds/record")
    // feedId로 record 가져오기
    // http://localhost:8080/feeds/record?feedId=6a9a17e91a334c3498213b6c89ac22c3
    public RecordResponseBody getFeedRecord(@RequestParam("feedId") String feedId){
        //        String encodedByteArrayAsString = Base64.getEncoder().encodeToString(recordResponseDto.getRecordRawData());
//        System.out.println("encodedByteArrayAsString: " + encodedByteArrayAsString);
//
//        byte[] decodedByteArray = Base64.getDecoder().decode(encodedByteArrayAsString);
//        System.out.println("decodedByteArray: " + decodedByteArray);
//
//        String resourcePath = "/Users/gunmo/Desktop/Team7-Backend/src/main/resources/static/output." + recordResponseDto.getRecordFileType().split("/")[1];
//        writeBytesToFile(decodedByteArray, resourcePath);

        return feedService.getRecordByFeedId(feedId);
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

    @DeleteMapping("/feeds/{feedId}")
    // feedId로 feed 삭제하기
    public ResponseObject deleteFeed(@PathVariable("feedId") String feedId){
        ServiceResult result = feedService.deleteFeedByFeedId(feedId);
        if(result.isFailed()) return ResponseObject.of(Status.BAD_REQUEST);
        return ResponseObject.of(Status.OK,feedId);
    }


}
