package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Record.RecordRepository;
import com.teamseven.MusicVillain.Status;
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
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;
    @Autowired
    public FeedService(FeedRepository feedRepository, RecordRepository recordRepository, MemberRepository memberRepository ){
        this.feedRepository = feedRepository;
        this.recordRepository = recordRepository;
        this.memberRepository = memberRepository;
    }
    public List<Feed> getAllFeeds(){
        return feedRepository.findAllByOrderByCreatedAtDesc();
    }

    public byte[] getRecordByFeedId(String feedId){
        // RecordResponseDto recordResponseDto = new RecordResponseDto();

        Feed feed = feedRepository.findByFeedId(feedId);
        if (feed == null){
//            recordResponseDto.setStatusCode(Status.BAD_REQUEST.getStatusCode());
//            recordResponseDto.setMessage("Feed not found");
//            recordResponseDto.setRecordId("");
//            recordResponseDto.setRecordRawData(new byte[]{});
            return new byte[]{};
        }

//        recordResponseDto.setStatusCode(Status.OK.getStatusCode());
//        recordResponseDto.setMessage("OK");
//        recordResponseDto.setRecordId(feed.getRecord().getRecordId());
//        recordResponseDto.setRecordRawData(feed.getRecord().getRecordRawData());
//        return recordResponseDto;
        return feed.getRecord().getRecordRawData();

    }
    public Map<Object, Object> insertFeed(String ownerId, String feedName, int recordDuration, byte[] recordRawData){
        Member feedOwner = memberRepository.findByMemberId(ownerId);

        Map<Object, Object> map = new HashMap();

        if (feedOwner == null){
            map.put("result", "fail");
            return map;
        }

        Record generatedRecord = Record.builder()
                .recordId(UUID.randomUUID().toString().replace("-", ""))
                .recordFileSize(recordRawData.length)
                .recordDuration(recordDuration)
                .recordFileType("")
                .recordRawData(recordRawData)
                .build();

        recordRepository.save(generatedRecord);

        String generatedFeedId = UUID.randomUUID().toString().replace("-", "");
        feedRepository.save(
                Feed.builder()
                        .feedId(generatedFeedId)
                        .feedName(feedName)
                        .owner(feedOwner)
                        .record(generatedRecord)
                        .interactionCount(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        map.put("result", "success");
        map.put("feedId", generatedFeedId);
        return map;
    }
    public Map<Object, Object> insertFeed2(String feedName, String ownerId, String feedDescription, int recordDuration, MultipartFile recordFile) throws IOException {
        Member feedOwner = memberRepository.findByMemberId(ownerId);

        Map<Object, Object> resultMap = new HashMap();

        if (feedOwner == null){
            System.out.println("[DEBUG] feedOwner is null");
            resultMap.put("result", "fail");
            return resultMap;
        }
        System.out.println("[DEBUG] feedName: " + feedName);
        System.out.println("[DEBUG] feedDescription: " + feedDescription);
        System.out.println("[DEBUG] ownerId: " + ownerId);
        System.out.println("[DEBUG] recordFileName: " + recordFile.getOriginalFilename());
        System.out.println("[DEBUG] recordFileSize: " + recordFile.getBytes().length);
        System.out.println("[DEBUG] recordFileType: " + recordFile.getContentType());
        System.out.println("[DEBUG] recordDuration: " + recordDuration);

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
                        .owner(feedOwner)
                        .record(generatedRecord)
                        .interactionCount(0)
                        .description(feedDescription)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        resultMap.put("result", "success");
        resultMap.put("feedId", generatedFeedId);
        return resultMap;
    }
    // [!] Implement Later
    public List<String> getFeedByMemberId(String memberId) {
        if(this.checkIsValidMemberId(memberId)==false){
            return null;
        }
        return null;
    }


    public boolean checkIsValidMemberId(String memberId){
        return memberRepository.findByMemberId(memberId) != null;
    }
}
