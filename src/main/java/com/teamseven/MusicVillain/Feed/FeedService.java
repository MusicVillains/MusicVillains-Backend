package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Interaction.InteractionRepository;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Record.RecordRepository;
import com.teamseven.MusicVillain.ServiceResult;
import com.teamseven.MusicVillain.Status;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public ServiceResult insertFeed(String feedName, String ownerId, String feedType, String feedDescription, int recordDuration, MultipartFile recordFile) throws IOException {
        Member feedOwner = memberRepository.findByMemberId(ownerId);

        if (feedOwner == null){
            return ServiceResult.fail("Member Not Found");
        }

        Record generatedRecord = Record.builder()
                .recordId(UUID.randomUUID().toString().replace("-", ""))
                .recordFileSize(recordFile.getBytes().length)
                .recordDuration(recordDuration)
                .recordFileType(recordFile.getContentType())
                .recordRawData(recordFile.getBytes())
                .build();
        recordRepository.save(generatedRecord);

        String generatedFeedId = UUID.randomUUID().toString().replace("-", "");

        feedRepository.save(
                Feed.builder()
                        .feedId(generatedFeedId)
                        .feedName(feedName)
                        .feedType(feedType)
                        .owner(feedOwner)
                        .record(generatedRecord)
                        .description(feedDescription)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        return ServiceResult.of(ServiceResult.SUCCESS, "Feed created", generatedFeedId);
    }
    // [!] Implement Later
    public List<String> getFeedByMemberId(String memberId) {
        if(this.checkIsValidMemberId(memberId)==false){
            return null;
        }
        return null;
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
}
