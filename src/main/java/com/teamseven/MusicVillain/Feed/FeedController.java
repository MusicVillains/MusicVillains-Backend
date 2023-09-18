package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Dto.ResponseBody.RecordResponseBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Security.JWT.MemberJwtAuthorizationManager;
import com.teamseven.MusicVillain.Security.JWT.AuthorizationResult;
import com.teamseven.MusicVillain.Security.JWT.FeedJwtAuthorizationManager;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Dto.DtoTest;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@RestController
@Tag(name = "피드 관련 API")
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

    /**
     * 모든 피드 조회 | GET | /feeds
     * @apiNote 데이터 베이스에 등록된 모든 피드 정보를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#getAllFeeds()
     *
     * @return 피드 정보 리스트 반환
     */
    @GetMapping("/feeds")
    @Operation(summary = "모든 피드 조회", description = "데이터 베이스에 등록된 모든 피드 정보를 조회합니다.")
    public ResponseObject getAllFeeds(){
        ServiceResult result = feedService.getAllFeeds();
        return ResponseObject.OK(result.getData());
    }

    /**
     * 피드 조회 | GET | /feeds/{feedId}
     * @apiNote 특정 피드를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#getFeedByFeedId(String)
     *
     * @param feedId 피드 아이디
     * @return 피드 DTO 반환
     */
    @GetMapping("/feeds/{feedId}")
    @Operation(summary = "피드 조회", description = "특정 피드를 조회합니다.")
    public ResponseObject getFeedById(@PathVariable("feedId") String feedId){
        return ResponseObject.OK(feedService.getFeedByFeedId(feedId));
    }

    /**
     * 타입별 피드 조회 | GET | /feeds/feedType?value={feedType}
     * @apiNote 타입 별로 피드를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#getAllFeedsByFeedType(String)
     *
     * @param feedType 피드 타입
     * @return 피드 리스트 반환
     */
    @GetMapping("/feeds/feedType")
    @Operation(summary = "타입별 피드 조회", description = "타입 별로 피드를 조회합니다.")
    // 주소 예시: http://localhost:8080/feeds/feedType?value=고음괴물
    public ResponseObject getAllFeedsByFeedType(@RequestParam("value") String feedType){
        ServiceResult result = feedService.getAllFeedsByFeedType(feedType);
        return ResponseObject.OK(result.getData());
    }

    /**
     * 피드 생성 | POST | /feeds
     * @apiNote 피드를 생성합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#insertFeed(String, String, String, int, MultipartFile, String, String)
     *
     * @param ownerId 피드 주인의 아이디
     * @param musicName 피드에 올라간 노래의 곡명
     * @param musicianName 피드에 올라간 노래의 가수
     * @param feedType 피드 타입
     * @param feedDescription 피드 설명
     * @param recordDuration 녹음본 파일의 길이
     * @param recordFile 녹음본 파일 (MultipartFile)
     *
     * @return [성공] 생성된 피드의 feedId 반환,
     *         [실패] 실패 메시지 반환
     * @throws IOException 파일 입출력 예외
     */
    @PostMapping(value = "/feeds", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "피드 생성", description = "새로운 피드를 생성합니다.")
    public ResponseObject createFeed(
            // MultipartFile 받으려면 @RequestParam 사용해야하므로 다음과 같이 form-data로 받도록 함.
            @RequestParam("ownerId") String ownerId,
            @RequestParam("musicName") String musicName,
            @RequestParam("musicianName") String musicianName,
            @RequestParam("feedType") String feedType,
            @RequestParam("description") String feedDescription,
            @RequestParam("recordDuration") int recordDuration,
//            @RequestParam("recordFile") MultipartFile recordFile,
            @Parameter(
                    description = "파일 업로드",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) @RequestPart("recordFile") MultipartFile recordFile,
            @RequestHeader HttpHeaders headers) throws IOException {

        log.trace("─> [FeedController] createFeed() called");
        log.trace("- Parameters:");
        log.trace("  - feedType: {}", feedType);
        log.trace("  - ownerId: {}", ownerId);
        log.trace("  - musicName: {}", musicName);
        log.trace("  - musicianName: {}", musicianName);
        log.trace("  - feedDescription: {}", feedDescription);
        log.trace("  - recordDuration: {}", recordDuration);
        log.trace("  - recordFile: {}",  recordFile);

        AuthorizationResult authResult = memberAuthManager.authorize(headers, ownerId);
        if(authResult.isFailed())
            return ResponseObject.UNAUTHORIZED(authResult.getMessage());

        ServiceResult result = feedService.insertFeed(ownerId, feedType,feedDescription, recordDuration, recordFile,musicName,musicianName);
        if (result.isFailed()) return ResponseObject.CREATION_FAIL(result.getData());
        log.trace("─> [FeedController] createFeed() finished");
        return ResponseObject.CREATED(result.getData());
    }

    /**
     * 피드 내용 수정 | PUT | /feeds<br>
     * @apiNote 피드 내용을 수정합니다.<br>
     * - 수정하고자 하는 필드와 수정하려는 값(value)를 `form-data` 형태로 요청합니다.<br>
     * - 피드 식별자(`feedId`)를 제외한 나머지 필드는 모두 선택적으로 수정할 수 있습니다.<br>
     * - 수정하지 않는 필드의 경우 생략하여 `null`로 요청합니다.
     * @see FeedService#modifyFeed(String, String, String, String, String, MultipartFile)
     * @author Woody K
     * @since JDK 17
     * @param feedId 피드 식별자(수정 대상)
     * @param feedType 수정하려는 피드 타입
     * @param feedDescription 수정하려는 피드 설명
     * @param musicName 수정하려는 음원명
     * @param musicianName 수정하려는 가수명
     * @param recordFile 수정하려는 파일 데이터
     * @param headers JWT 토큰을 포함한 헤더
     * @return [성공] 수정된 피드 정보 반환<br>[실패] 실패 메시지 반환
     */
    @PutMapping(value = "/feeds", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseObject modifyFeed(@RequestParam(name = "feedId", required = true) String feedId,
                                     @RequestParam(name = "feedType", required = false) String feedType,
                                     @RequestParam(name = "description", required = false) String feedDescription,
                                     @RequestParam(name = "musicName", required = false) String musicName,
                                     @RequestParam(name = "musicianName", required = false) String musicianName,
//                                     @RequestParam(name = "recordFile",  required = false) MultipartFile recordFile,
                                     @Parameter(
                                             description = "파일 업로드",
                                             content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                                     ) @RequestPart("recordFile") MultipartFile recordFile,
                                     @RequestHeader HttpHeaders headers){

        AuthorizationResult authResult = feedAuthManager.authorize(headers, feedId);
        if(authResult.isFailed())
            return ResponseObject.UNAUTHORIZED(authResult.getMessage());

        log.info("feedType: {}", feedType);
        if(recordFile != null) {
            log.info("recordFile.getSize(): {}", recordFile.getSize());
            log.info("recordFile.getName(): {}", recordFile.getName());
            log.info("recordFile.isEmpty(): {}", recordFile.isEmpty());
        }

        ServiceResult result = feedService.
                modifyFeed(feedId, feedType, feedDescription, musicName, musicianName, recordFile);

        return result.isFailed() ? ResponseObject.BAD_REQUEST(result.getData())
                : ResponseObject.OK(result.getData());
    }

    /**
     * 피드 녹음본 조회 | GET | /feeds/record?feedId={feedId}
     * @apiNote 특정 피드에서 녹음본을 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#getRecordByFeedId(String)
     *
     * @param feedId 피드 아이디
     * @return 녹음본 반환
     */
    @GetMapping("/feeds/record")
    @Operation(summary = "피드 녹음본 조회", description = "특정 피드에서 녹음본을 조회합니다.")
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
    @Hidden
    public static void writeBytesToFile(byte[] data, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(data);
            log.debug("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace(); }
    }

    /**
     * 멤버별 피드 조회 | GET | /feeds/member?id={memberId}
     * @apiNote 멤버가 생성한 모든 피드를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#getAllFeedsByMemberId(String)
     *
     * @param memberId 멤버 아이디
     * @return 피드 리스트 반환
     */
    @GetMapping("/feeds/member")
    @Operation(summary = "멤버가 생성한 모든 피드 조회", description = "회원이 생성한 모든 피드를 조회합니다.")
    // 주소 예시: http://localhost:8080/feeds/member?id=1
    public ResponseObject getFeedByMemberId(@RequestParam("id") String memberId){
        ServiceResult result = feedService.getAllFeedsByMemberId(memberId);
        return ResponseObject.OK(result.getData());
    }

    /**
     * 피드 삭제 | DELETE | /feeds/{feedId}
     * @apiNote 피드를 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#deleteFeedByFeedId(String)
     *
     * @param feedId 삭제할 피드의 아이디
     * @param headers 헤더
     *
     * @return [성공] 삭제된 피드의 feedId 반환,
     *         [실패] 실패 메시지 반환
     */
    @DeleteMapping("/feeds/{feedId}")
    @Operation(summary = "피드 삭제", description = "특정 피드를 삭제합니다.")
    public ResponseObject deleteFeed(@PathVariable("feedId") String feedId,
                                         @RequestHeader HttpHeaders headers){
        log.debug("─> [FeedController] deleteFeed() called");
        AuthorizationResult authResult = feedAuthManager.authorize(headers,feedId);
        if(authResult.isFailed())
            return ResponseObject.UNAUTHORIZED(authResult.getMessage());

        ServiceResult result = feedService.deleteFeedByFeedId(feedId);
        if(result.isFailed()) return ResponseObject.BAD_REQUEST();
        return ResponseObject.OK(feedId);
    }

    /**
     * 피드 조회수 증가 | POST | /feeds/viewFeed?id={feedId}
     * @apiNote 피드 조회수를 증가시킵니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#feedViewCountUp(String)
     *
     * @param feedId 조회수를 증가시킬 피드의 아이디
     * @return [성공] 조회수 증가된 피드의 아이디 반환,
     *         [실패] 실패 메시지 반환
     *
     */
    @PostMapping("/feeds/viewFeed")
    @Operation(summary = "피드 조회수 증가", description = "특정 피드의 조회수를 증가시킵니다.")
    // url: http://localhost:8080/feeds/viewFeed?id={feedId}
    public ResponseObject viewCountUp(@RequestParam("id") String feedId){
        ServiceResult result = feedService.feedViewCountUp(feedId);
        return result.isFailed() ? ResponseObject.BAD_REQUEST(result.getData())
                : ResponseObject.OK(result.getData());
    }

    /**
     * 상호작용 피드 조회 | GET | /feeds/interactionFeeds?memberId={memberId}
     * @apiNote 회원 본인이 좋아요를 누른 모든 피드를 조회합니다.
     *
     * @author Woody K
     * @since JDK 17
     * @see FeedService#getInteractionFeedsByMemberId(String)
     *
     * @param memberId 멤버 아이디
     * @param headers 헤더
     * @return [성공] 상호작용 피드 리스트 반환,
     *         [실패] 실패 메시지 반환
     */
    // [!] Check Needed!
    @GetMapping("/feeds/interactionFeeds")
    @Operation(summary = "상호작용 피드 조회", description = "회원 본인이 좋아요를 누른 모든 피드를 조회합니다.")
    public ResponseObject getInteractionFeedsByMemberId(@RequestParam("memberId") String memberId,
                                                            @RequestHeader HttpHeaders headers){

        AuthorizationResult authResult = memberAuthManager.authorize(headers, memberId);
        if(authResult.isFailed())
            return ResponseObject.UNAUTHORIZED(authResult.getMessage());

        ServiceResult result = feedService.getInteractionFeedsByMemberId(memberId);
        return result.isFailed() ? ResponseObject.BAD_REQUEST(result.getData())
                : ResponseObject.OK(result.getData());
    }

    @GetMapping("/dev/test/getTestFeed")
    @Hidden
    // 테스트용 피드 가져오기
    // 주소 예시: http://localhost:8080/dev/test/getTestFeed
    public ResponseObject getTestFeed() {
        if (DtoTest.feed == null) {
            return ResponseObject.of(Status.BAD_REQUEST, "Test feed not initialized.");
        }
        return ResponseObject.of(Status.OK, DtoTest.feedDto);
    }

}
