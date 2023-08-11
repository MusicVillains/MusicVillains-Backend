package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Interaction.Interaction;
import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Record.RecordRepository;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Status;
import com.teamseven.MusicVillain.Util.RandomUUIDGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.teamseven.MusicVillain.Record.Record;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final InteractionRepository interactionRepository;
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    @Autowired
    public FeedService(FeedRepository feedRepository, RecordRepository recordRepository, MemberRepository memberRepository, InteractionRepository interactionRepository) {
        this.feedRepository = feedRepository;
        this.recordRepository = recordRepository;
        this.memberRepository = memberRepository;
        this.interactionRepository = interactionRepository;
    }
    public ServiceResult getAllFeeds(){

        List<FeedDto> feedDtoList = FeedDto.toFeedDtoList(
                feedRepository.findAllByOrderByCreatedAtDesc());
        return ServiceResult.success(feedDtoList);
    }

    public RecordResponseBody getRecordByFeedId(String feedId){

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

    public ServiceResult insertFeed(String feedName, String ownerId, String feedType, String feedDescription, int recordDuration, MultipartFile recordFile,
                                    String musicName, String musicianName
    ) throws IOException {

        System.out.println("─> [FeedService] insertFeed() called");
        System.out.println("- Parameters:");
        System.out.println("  - feedName: " + feedName);
        System.out.println("  - ownerId: " + ownerId);
        System.out.println("  - feedType: " + feedType);
        System.out.println("  - feedDescription: " + feedDescription);
        System.out.println("  - recordDuration: " + recordDuration);
        System.out.println("  - recordFile: " + recordFile);
        System.out.println("  - musicName: " + musicName);
        System.out.println("  - musicianName: " + musicianName);

        Member feedOwner = memberRepository.findByMemberId(ownerId);

        if (feedOwner == null){
            System.out.println("[!] insertFeed() failed: Member Not Found");
            System.out.println("─> [FeedService] Exit insertFeed() with exception");
            return ServiceResult.fail("Member Not Found");
        }

        if(feedName == null || ownerId == null || feedType == null){
            System.out.println("[!] insertFeed() failed: missing parameter");
            System.out.println("─> [FeedService] Exit insertFeed() with exception");
            return ServiceResult.fail("missing parameter");
        }

        Record generatedRecord = Record.builder()
                .recordId(RandomUUIDGenerator.generate())
                .recordFileSize(recordFile.getBytes().length)
                .recordDuration(recordDuration)
                .recordFileType(recordFile.getContentType())
                .recordRawData(recordFile.getBytes())
                .build();
        recordRepository.save(generatedRecord);
        System.out.println("[>] Record created and saved to database");

        String generatedFeedId = RandomUUIDGenerator.generate();

        feedRepository.save(
                Feed.builder()
                        .feedId(generatedFeedId)
                        .feedName(feedName)
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

    public ServiceResult getAllFeedsByMemberId(String memberId) {
        Member member = memberRepository.findByMemberId(memberId);

        List<FeedDto> resultFeedEntityList = FeedDto.toFeedDtoList(feedRepository.findAllByOwnerMemberId(memberId));

        return ServiceResult.success(resultFeedEntityList);

    }

    public ServiceResult feedViewCountUp(String feedId){
        Feed feed = feedRepository.findByFeedId(feedId);
        if(feed == null){
            return ServiceResult.of(ServiceResult.FAIL, "Feed Not Found");
        }
        feed.viewCount++;
        feedRepository.save(feed);
        return ServiceResult.of(ServiceResult.SUCCESS, "Feed View Count Up", feed.viewCount);
    }

    @Transactional
    public ServiceResult deleteFeedByFeedId(String feedId){
        Feed feedToDelete = feedRepository.findByFeedId(feedId);
        // 존재하는 피드인지 확인
        if(feedToDelete == null){
            return ServiceResult.of(ServiceResult.FAIL, "Feed Not Found");}

        // 피드에 대한 Interaciton 삭제
        interactionRepository.deleteByInteractionFeedFeedId(feedToDelete.feedId);
        // Feed 삭제, Record는 Feed쪽에서 CASCADE로 자동 삭제됨
        feedRepository.deleteByFeedId(feedId);
        return ServiceResult.of(ServiceResult.SUCCESS, "Feed Deleted", feedId);

    }

    public boolean checkIsValidMemberId(String memberId){
        return memberRepository.findByMemberId(memberId) != null;
    }

    public FeedDto getFeedByFeedId(String feedId) {
        return FeedDto.toFeedDto(feedRepository.findByFeedId(feedId));
    }


    public ServiceResult getInteractionFeedsByMemberId(String memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        if(member == null){
            return ServiceResult.of(ServiceResult.FAIL, "Member Not Found");
        }
        List<Interaction> interactionList = interactionRepository.findAllByInteractionMemberMemberId(memberId);

        List<Feed> interactionFeedList = new ArrayList<>();

        for(Interaction interaction : interactionList){
            interactionFeedList.add(interaction.getInteractionFeed());
        }

        return ServiceResult.success(FeedDto.toFeedDtoList(interactionFeedList));

    }

    public ServiceResult getFeedOwnerMemberIdByFeedId(String feedId) {

        Feed tmpFeed = feedRepository.findByFeedId(feedId);
        if(tmpFeed == null) return ServiceResult.fail("Feed Not Found");
        return ServiceResult.success(tmpFeed.getOwner().getMemberId());

    }
}
