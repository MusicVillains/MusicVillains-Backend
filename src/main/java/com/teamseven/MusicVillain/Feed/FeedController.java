package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ResponseBody.RecordResponseBody;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Security.JWT.FeedJwtAuthorizationManager;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.io.FileOutputStream;
import java.io.IOException;

@RestController

public class FeedController {
    private final FeedService feedService;
    private FeedJwtAuthorizationManager feedAuthManager;
    private MemberJwtAuthorizationManager memberAuthManager;
    @Autowired
    public FeedController(FeedService feedService, FeedJwtAuthorizationManager authManager,
                          MemberJwtAuthorizationManager memberAuthManager){
        this.feedService = feedService;
        this.feedAuthManager = authManager;
        this.memberAuthManager = memberAuthManager;
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
            @RequestParam("musicName") String musicName,
            @RequestParam("musicianName") String musicianName,
            @RequestParam("description") String feedDescription,
            @RequestParam("recordDuration") int recordDuration,
            @RequestParam("recordFile") MultipartFile recordFile) throws IOException {
        System.out.println("─> [FeedController] createFeed() called");
        System.out.println("- Parameters:");
        System.out.println("  - feedName: " + feedName);
        System.out.println("  - feedType: " + feedType);
        System.out.println("  - ownerId: " + ownerId);
        System.out.println("  - musicName: " + musicName);
        System.out.println("  - musicianName: " + musicianName);
        System.out.println("  - feedDescription: " + feedDescription);
        System.out.println("  - recordDuration: " + recordDuration);
        System.out.println("  - recordFile: " + recordFile);

        ServiceResult result = feedService.insertFeed(feedName, ownerId, feedType,feedDescription, recordDuration, recordFile,musicName,musicianName);
        if (result.isFailed()) return ResponseObject.of(Status.CREATION_FAIL, result.getData());
        System.out.println("─> [FeedController] createFeed() finished");
        return ResponseObject.of(Status.OK, result.getData());
    }
    @GetMapping("/feeds/record")
    // feedId로 record 가져오기
    // http://localhost:8080/feeds/record?feedId=6a9a17e91a334c3498213b6c89ac22c3
    public RecordResponseBody getFeedRecord(@RequestParam("feedId") String feedId){
//        RecordResponseBody recordResponseDto = feedService.getRecordByFeedId(feedId);
//
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
            e.printStackTrace(); }
    }

    @GetMapping("/feeds/member")
    // 멤버가 생성한 모든 피드 조회
    // 주소 예시: http://localhost:8080/feeds/member?id=1
    public ResponseObject getFeedByMemberId(@RequestParam("id") String memberId){
        ServiceResult result = feedService.getAllFeedsByMemberId(memberId);
        return ResponseObject.of(Status.OK,result.getData());
    }

    @DeleteMapping("/feeds/{feedId}")
    // feedId로 feed 삭제하기
    public ResponseObject deleteFeed(@PathVariable("feedId") String feedId,
    @RequestHeader HttpHeaders headers){
        System.out.println("─> [FeedController] deleteFeed() called");
        AuthorizationResult authResult = feedAuthManager.authorize(headers,feedId);
        if(authResult.isFailed())
            return ResponseObject.of(Status.UNAUTHORIZED,authResult.getMessage());

        ServiceResult result = feedService.deleteFeedByFeedId(feedId);
        if(result.isFailed()) return ResponseObject.of(Status.BAD_REQUEST);
        return ResponseObject.of(Status.OK,feedId);
    }
    @PostMapping("/feeds/viewFeed")
    // url: http://localhost:8080/feeds/viewFeed?id={feedId}
    // feed 조회수 증가
    public ResponseObject viewCountUp(@RequestParam("id") String feedId){
        ServiceResult result = feedService.feedViewCountUp(feedId);
        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST,result.getData())
                : ResponseObject.of(Status.OK,result.getData());
    }

    // [!] Check Needed!
    @GetMapping("/feeds/interactionFeeds")
    public ResponseObject getInteractionFeedsByMemberId(@RequestParam("memberId") String memberId,
                                                        @RequestHeader HttpHeaders headers){

        AuthorizationResult authResult = memberAuthManager.authorize(headers, memberId);
        if(authResult.isFailed())
            return ResponseObject.of(Status.UNAUTHORIZED,authResult.getMessage());

        ServiceResult result = feedService.getInteractionFeedsByMemberId(memberId);
        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST,result.getData())
                : ResponseObject.of(Status.OK,result.getData());
    }

}
