package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Dto.*;
import com.teamseven.MusicVillain.Dto.Converter.DtoConverter;
import com.teamseven.MusicVillain.Dto.Converter.DtoConverterFactory;
import com.teamseven.MusicVillain.Dto.Converter.FeedDtoConverter;
import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Interaction.InteractionService;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Notification.NotificationRepository;
import com.teamseven.MusicVillain.Record.RecordRepository;
import com.teamseven.MusicVillain.Dto.ResponseBody.RecordResponseBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.teamseven.MusicVillain.Record.Record;
import org.springframework.web.multipart.MultipartFile;


@SuppressWarnings("all")
@Service
@Slf4j
public class FeedService {
    private final FeedRepository feedRepository;
    private final InteractionRepository interactionRepository;
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final InteractionService interactionService;

    @Autowired
    public FeedService(FeedRepository feedRepository, RecordRepository recordRepository,
                       MemberRepository memberRepository, InteractionRepository interactionRepository,
                       NotificationRepository notificationRepository, InteractionService interactionService) {
        this.feedRepository = feedRepository;
        this.recordRepository = recordRepository;
        this.memberRepository = memberRepository;
        this.interactionRepository = interactionRepository;
        this.notificationRepository = notificationRepository;
        this.interactionService = interactionService;
    }

    private DtoConverter dtoConverter =
            DtoConverterFactory.getConverter(Feed.class, FeedDto.class);


    private List<Feed> getAllFeeds(){
        return feedRepository.findAllByOrderByCreatedAtDesc();
    }

    private List<Feed> getAllFeedsByFeedType(String feedType){

        log.info("Request feedType: " + feedType);
        if(feedType.equals("all") || feedType.equals("전체")){
            return this.getAllFeeds();
        }
        List<Feed> FeedList = feedRepository.findAllByFeedType(feedType);
        return FeedList;
    }

    private List<Feed> getAllFeedsByMemberId(String memberId){
        return feedRepository.findAllByOwnerMemberId(memberId);
    }

    private Feed getFeedByFeedId(String feedId){
        return feedRepository.findByFeedId(feedId);
    }

    /**
     * @see com.teamseven.MusicVillain.Dto.FeedDto
     * @see com.teamseven.MusicVillain.Dto.InteractionProps
     * @apiNote
     * 로그인 하지 않은 사용자 들에 대한 피드 조회 요청을 처리한다.<br>
     * 모든 피드를 생성된 시간의 내림차순으로 반환한다.<br>
     * 로그인 하지 않은 사용자에 대해서는 FeedDto의 InteractionProps 필드가 Interaction이 수행되지 않은 상태로 반환된다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @return ServiceResult 객체. 성공시 모든 FeedDto 객체의 리스트를 포함.
     */
    public ServiceResult serviceAllFeeds(){
        List<FeedDto> feedDtoList = dtoConverter.convertToDtoList(this.getAllFeeds());
        return ServiceResult.success(feedDtoList);
    }

    /**
     * @see com.teamseven.MusicVillain.Dto.Converter.FeedDtoConverter#convertToDtoListForMember(String, List)
     * @see com.teamseven.MusicVillain.Dto.FeedDto
     * @see com.teamseven.MusicVillain.Dto.InteractionProps
     *
     * @apiNote
     * 로그인 한 멤버에 대한 피드 조회 요청을 처리한다.<br>
     * 사용자가 Interaction을 수행한 피드와 수행하지 않은 피드를 구분하여 FeedDto의 InteractionProps 필드를 설정하여 반환한다.<br>
     * @param authorizedMemberId 조회를 요청한 회원 식별자
     * @return ServiceResult 객체. 성공시 모든 FeedDto 객체의 리스트를 포함.
     */
    public ServiceResult serviceAllFeeds(String authorizedMemberId){

        List<FeedDto> feedDtoList =
                ((FeedDtoConverter)dtoConverter).convertToDtoListForMember(authorizedMemberId, this.getAllFeeds());

        return ServiceResult.success(feedDtoList);
    }
    /**
     * 지정된 피드 ID와 연관된 레코드를 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 레코드 정보를 가져올 피드의 ID
     * @return RecordResponseBody 객체. 레코드의 세부 정보를 포함.
     */
    public RecordResponseBody serviceRecordByFeedId(String feedId){
        if(feedId == null) return RecordResponseBody.builder()
                .statusCode(Status.BAD_REQUEST.getStatusCode())
                .message("FeedId is null")
                .build();
        Feed feed = feedRepository.findByFeedId(feedId);
        if (feedRepository.findByFeedId(feedId) == null) return RecordResponseBody.builder()
                .statusCode(Status.NOT_FOUND.getStatusCode())
                .message("Feed not found")
                .build();

        return RecordResponseBody.builder()
                .statusCode(Status.OK.getStatusCode())
                .message("Record found")
                .recordId(feed.getRecord().getRecordId())
                .recordFileSize(feed.getRecord().getRecordFileSize())
                .recordDuration(feed.getRecord().getRecordDuration())
                .recordFileType(feed.getRecord().getRecordFileType())
                .recordRawData(feed.getRecord().getRecordRawData())
                .build();
    }

    /**
     * 새로운 피드를 데이터베이스에 삽입합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param ownerId 피드의 소유자 ID
     * @param feedType 피드의 타입
     * @param feedDescription 피드의 설명
     * @param recordDuration 레코드의 지속 시간
     * @param recordFile 레코드 파일
     * @param musicName 음악의 이름
     * @param musicianName 음악가의 이름
     * @return ServiceResult 객체. 성공시 생성된 피드 ID를 포함.
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public ServiceResult insertFeed(String ownerId, String feedType, String feedDescription, int recordDuration, MultipartFile recordFile,
                                    String musicName, String musicianName) throws IOException {
        log.debug("insertFeed() called");
        log.debug("- Parameters:");
        log.debug("  - ownerId: " + ownerId);
        log.debug("  - feedType: " + feedType);
        log.debug("  - feedDescription: " + feedDescription);
        log.debug("  - recordDuration: " + recordDuration);
        log.debug("  - recordFile: " + recordFile);
        log.debug("  - musicName: " + musicName);
        log.debug("  - musicianName: " + musicianName);

        Member feedOwner = memberRepository.findByMemberId(ownerId);

        if (feedOwner == null){
            log.debug("[!] insertFeed() failed: Member Not Found");
            log.debug("─> [FeedService] Exit insertFeed() with exception");
            return ServiceResult.fail("Member Not Found");
        }

        if( musicName == null || ownerId == null || feedType == null){
            log.debug("[!] insertFeed() failed: missing parameter");
            log.debug("─> [FeedService] Exit insertFeed() with exception");
            return ServiceResult.fail("missing parameter");
        }

        Record generatedRecord = Record.builder()
                .recordId(RandomUUIDGenerator.generate())
                //.recordFileSize(recordFile.getBytes().length)
                .recordDuration(recordDuration)
                //.recordFileType(recordFile.getContentType())
                //.recordRawData(recordFile.getBytes())
                .build();
        // NPE 방지
        if(recordFile == null) {
            generatedRecord.setRecordFileType("Empty");
            generatedRecord.setRecordFileSize(0);
            generatedRecord.setRecordRawData(null);

        }else{
            generatedRecord.setRecordFileType(recordFile.getContentType());
            generatedRecord.setRecordFileSize(recordFile.getBytes().length);
            generatedRecord.setRecordRawData(recordFile.getBytes());
        }

        recordRepository.save(generatedRecord);
        System.out.println("[>] Record created and saved to database");

        String generatedFeedId = RandomUUIDGenerator.generate();

        feedRepository.save(
                Feed.builder()
                        .feedId(generatedFeedId)
                        .feedType(feedType)
                        .owner(feedOwner)
                        .record(generatedRecord)
                        .description(feedDescription)
                        .viewCount(0)
                        .musicName(musicName)
                        .musicianName(musicianName)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        System.out.println("[>] Feed created and saved to database");
        System.out.println("─> [FeedService] insertFeed() successfully finished");
        return ServiceResult.of(ServiceResult.SUCCESS, "Feed created", generatedFeedId);
    }

    /**
     * 피드를 수정합니다.<br>
     * 수정할 피드의 식별자를 제외한 모든 매개변수는 null일 수 있습니다.<br>
     * null인 매개변수는 수정되지 않습니다.<br>
     * @author Woody K
     * @since JDK 17
     * @param feedId 수정할 피드의 식별자
     * @param feedType 수정할 피드 타입
     * @param feedDescription 수정할 피드 설명
     * @param recordDuration 수정할 레코드 시간
     * @param recordFile 수정할 레코드 파일
     * @param musicName 수정할 음원 이름
     * @param musicianName 수정할 음악가 이름
     * @return ServiceResult<br><b>* 성공</b> - 수정된 피드 정보 반환<br><b>* 실패</b> - 실패 메시지 반환
     */
    @Transactional
    public ServiceResult modifyFeed(String feedId, String feedType, String feedDescription,
                                    String musicName, String musicianName, MultipartFile recordFile){
        log.debug("modifyFeed() called");

        // feedId null check
        if(feedId == null){
            log.debug("[!] modifyFeed() failed: feedId is null");
            log.debug("─> [FeedService] Exit modifyFeed() with exception");
            return ServiceResult.fail("feedId is null");
        }

        // feedId로 Feed 찾기
        Feed nullableFeed = feedRepository.findByFeedId(feedId);

        // feedId로 Feed가 존재하는지 확인
        if(nullableFeed == null){
            log.debug("[!] modifyFeed() failed: Feed Not Found");
            log.debug("─> [FeedService] Exit modifyFeed() with exception");
            return ServiceResult.fail("Feed Not Found");
        }

        // null이 아닌 매개변수만 수정
        if(feedType != null && !feedType.isEmpty()) nullableFeed.setFeedType(feedType);
        if(feedDescription != null && !feedDescription.isEmpty()) nullableFeed.setDescription(feedDescription);
        if(musicName != null && !musicName.isEmpty()) nullableFeed.setMusicName(musicName);
        if(musicianName != null && !musicianName.isEmpty()) nullableFeed.setMusicianName(musicianName);

        // recordFile이 null이 아닌 경우에만 수정
        if(recordFile != null && !recordFile.isEmpty()){
            log.info("recordFile is not null");
            Record feedRecord = nullableFeed.getRecord();
            if(feedRecord == null){
                log.debug("[!] modifyFeed() failed: Original Record Not Found");
                log.debug("─> [FeedService] Exit modifyFeed() with exception");
                return ServiceResult.fail("Original Record Not Found");
            }
            try {
                // 기존 Record 갱신
                feedRecord.setRecordRawData(recordFile.getBytes());
                feedRecord.setRecordFileSize(recordFile.getBytes().length);
                feedRecord.setRecordFileType(recordFile.getContentType());
                recordRepository.save(feedRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            log.info("recordFile is null, not modified");

        }

        feedRepository.save(nullableFeed);

        return ServiceResult.success(
                dtoConverter.convertToDto(nullableFeed));
    }

    /**
     * @apiNote
     * 로그인 완료한 회원의 회원 식별자로 해당 회원이 생성한 모든 피드를 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 피드를 조회할 멤버의 ID
     * @return ServiceResult 객체. 성공시 해당 멤버의 모든 FeedDto 객체의 리스트를 포함.
     *
     * @see com.teamseven.MusicVillain.Dto.Converter.FeedDtoConverter#convertToDtoListForMember(String, List)
     */
    public ServiceResult serviceAllFeedsByAuthorizedMemberId(String authorizedMemberid) {
        if(authorizedMemberid == null) return ServiceResult.fail("MemberId is null");

        List<Feed> feeds = this.getAllFeedsByMemberId(authorizedMemberid);

        if (feeds == null)
            return ServiceResult.fail("Feeds not found for memberId: " + authorizedMemberid);

        List<FeedDto> resultFeedDtoList =
                ((FeedDtoConverter) dtoConverter).convertToDtoListForMember(authorizedMemberid,feeds);

        return ServiceResult.success(resultFeedDtoList);
    }


    /**
     * 로그인하지 않은 사용자의 요청에 대해 지정된 피드 타입의 모든 피드를 반환합니다.
     *
     * @since JDK 17
     *
     * @param feedType 조회할 피드의 타입
     * @return ServiceResult 객체. 성공시 해당 타입의 모든 FeedDto 객체의 리스트를 포함.
     */
    public ServiceResult serviceAllFeedsByFeedType(String feedType){
        if (feedType == null)
            return ServiceResult.fail("FeedType is null");

        List<Feed> feeds = this.getAllFeedsByFeedType(feedType);

        if (feeds == null)
            return ServiceResult.fail("Feeds not found for feedType: " + feedType);

        List<FeedDto> resultFeedDtoList = dtoConverter.convertToDtoList(feeds);

        return ServiceResult.success(resultFeedDtoList);
    }


    /**
     * 로그인한 사용자의 요청에 대해서 지정된 피드 타입의 모든 피드를 반환합니다.
     *
     * @since JDK 17
     * @param authorizedMemberId 조회를 요청한 회원 식별자
     * @param feedType 조회할 피드의 타입
     * @return ServiceResult 객체. 성공시 해당 타입의 모든 FeedDto 객체의 리스트를 포함.
     */
    public ServiceResult serviceAllFeedsByFeedType(String authorizedMemberId, String feedType){
        if (feedType == null) return ServiceResult.fail("FeedType is null");

        List<Feed> feeds = this.getAllFeedsByFeedType(feedType);

        if (feeds == null)
            return ServiceResult.fail("Feeds not found for feedType: " + feedType);

        List<FeedDto> resultFeedDtoList =
                ((FeedDtoConverter) dtoConverter).convertToDtoListForMember(authorizedMemberId, feeds);

        return ServiceResult.success(resultFeedDtoList);
    }

    /**
     * 지정된 피드의 조회수를 1 증가시킵니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 조회수를 증가시킬 피드의 ID
     * @return ServiceResult 객체. 성공시 증가된 조회수를 포함.
     */
    public ServiceResult feedViewCountUp(String feedId){
        if(feedId == null) return ServiceResult.fail("FeedId is null");

        Feed feed = feedRepository.findByFeedId(feedId);

        if(feed == null){
            return ServiceResult.of(ServiceResult.FAIL, "Feed Not Found");
        }
        feed.viewCount++;
        feedRepository.save(feed);
        return ServiceResult.of(ServiceResult.SUCCESS, "Feed View Count Up", feed.viewCount);
    }

    /**
     * 지정된 피드 ID의 피드를 삭제합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 삭제할 피드의 ID
     * @return ServiceResult 객체. 성공 또는 실패 메시지를 포함.
     */
    @Transactional
    public ServiceResult deleteFeedByFeedId(String feedId){
        // parameter null check
        if(feedId == null) {
            log.debug("[!] deleteFeedByFeedId() failed: Bad request, feedId is null");
            return ServiceResult.fail("Bad request, feedId is null");
        }

        System.out.println("Enter FeedService.deleteFeedByFeedId()");
        Feed nullableFeedToDelete = feedRepository.findByFeedId(feedId);
        // 존재하는 피드인지 확인
        if(nullableFeedToDelete == null){
            log.debug("[!] deleteFeedByFeedId() failed: Feed Not Found");
            return ServiceResult.of(ServiceResult.FAIL, "Feed Not Found");}

        /* interactionService.deleteInterationsByFeedId에서
        내부적으로 각 인터렉션과 관련된 Notification도 삭제됨 */
        interactionService.deleteInterationsByFeedId(feedId);

        // Feed 삭제, Record는 Feed가 삭제되면 CASCADE로 자동 삭제됨
        feedRepository.deleteByFeedId(feedId);
        System.out.println("Exit FeedService.deleteFeedByFeedId()");
        return ServiceResult.of(ServiceResult.SUCCESS, "Feed Deleted", feedId);

    }

    /**
     * 지정된 피드 ID에 대한 피드 정보를 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 정보를 가져올 피드의 ID
     * @return FeedDto 객체. 해당 피드의 정보를 포함.
     */
    public ServiceResult serviceFeedByFeedId(String feedId) {
        // parameter null check
        if(feedId == null)
            return ServiceResult.fail("Bad request, feedId is null");

        // find feed by feedId
        Feed nullableFeed = this.getFeedByFeedId(feedId);

        // check if feed not found
        if(nullableFeed == null)
            return ServiceResult.fail("Feed Not Found");

        // convert to FeedDto
        DataTransferObject feedDto = dtoConverter.convertToDto(nullableFeed);

        return ServiceResult.success(feedDto);
    }

    public ServiceResult serviceFeedByFeedId(String authorizedMemberId, String feedId) {
        // parameter null check
        if(feedId == null)
            return ServiceResult.fail("Bad request, feedId is null");

        // find feed by feedId
        Feed nullableFeed = this.getFeedByFeedId(feedId);

        // check if feed not found
        if(nullableFeed == null)
            return ServiceResult.fail("Feed Not Found");

        // convert to FeedDto
        DataTransferObject feedDto
                = ((FeedDtoConverter)dtoConverter).convertToDtoForMember(authorizedMemberId, nullableFeed);

        return ServiceResult.success(feedDto);
    }

    /**
     * @apiNote
     * 로그인한 회원 또는 인가가 완료된 경우에만 사용가능합니다.<br>
     * 특정 회원이 상호작용한 모든 피드를 반환합니다.<br>
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param authorizedMemberId 요청한 회원의 식별자
     * @return ServiceResult 객체. 성공시 해당 멤버가 상호 작용한 모든 FeedDto 객체의 리스트를 포함.
     *
     * @see com.teamseven.MusicVillain.Dto.Converter.FeedDtoConverter#convertToDtoListForMember(String, List)
     */
    public ServiceResult serviceInteractionFeedsByAuthorizedMemberId(String authorizedMemberId) {
        // parameter null check
        if(authorizedMemberId == null) return ServiceResult.fail("Bad request, memberId is null");

        // get Member from DB
        Member nullableMember = memberRepository.findByMemberId(authorizedMemberId);
        // check if member is null
        if(nullableMember == null) return ServiceResult.of(ServiceResult.FAIL, "Member Not Found");

        // get Interaction List of this Member from DB
        List<Interaction> interactionList =
                interactionRepository.findAllByInteractionMemberMemberId(authorizedMemberId);

        List<Feed> interactionFeedList = new ArrayList<>();

        for(Interaction interaction : interactionList){
            interactionFeedList.add(interaction.getInteractionFeed());
        }

        List<FeedDto> resultFeedDtoList = ((FeedDtoConverter) dtoConverter).convertToDtoListForMember(authorizedMemberId, interactionFeedList);
        return ServiceResult.success(resultFeedDtoList);

    }

    /**
     * 지정된 피드 ID의 피드 소유자 멤버 ID를 반환합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param feedId 소유자의 멤버 ID를 가져올 피드의 ID
     * @return ServiceResult 객체. 성공시 해당 피드의 소유자 멤버 ID를 포함.
     */
    public ServiceResult serviceFeedOwnerMemberIdByFeedId(String feedId) {
        // check if
        if (feedId == null) return ServiceResult.fail("FeedId is null");

        Feed nullableFeed = feedRepository.findByFeedId(feedId);
        if(nullableFeed == null) return ServiceResult.fail("Feed Not Found");

        return ServiceResult.success(nullableFeed.getOwner().getMemberId());

    }


    /**
     * 멤버 ID가 유효한지 검사합니다.
     *
     * @author Woody K
     * @since JDK 17
     *
     * @param memberId 검사할 멤버의 ID
     * @return boolean. 유효한 멤버 ID인 경우 true, 그렇지 않은 경우 false.
     */
    public boolean checkIsValidMemberId(String memberId){
        if (memberId == null) return false;
        return memberRepository.findByMemberId(memberId) != null;
    }
}
