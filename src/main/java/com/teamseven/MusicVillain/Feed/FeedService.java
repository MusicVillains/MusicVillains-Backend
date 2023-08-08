package com.teamseven.MusicVillain.Feed;

import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Record.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.teamseven.MusicVillain.Record.Record;
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
        return feedRepository.findAll();
    }

    public Map<Object, Object> insertFeed(String ownerId, String feedName, String recordName, String recordFileType, int recordDuration, byte[] recordRawData){
        Member feedOwner = memberRepository.findByMemberId(ownerId);

        Map<Object, Object> map = new HashMap();


        if (feedOwner == null){
            map.put("result", "fail");
            return map;
        }

        Record generatedRecord = Record.builder()
                .recordId(UUID.randomUUID().toString().replace("-", ""))
                .recordName(recordName)
                .recordFileSize(recordRawData.length)
                .recordDuration(recordDuration)
                .recordFileType(recordFileType)
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


}
