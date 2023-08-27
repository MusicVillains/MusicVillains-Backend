package com.teamseven.MusicVillain.Dto;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Dto.Converter.FeedDtoConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Record.Record;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DtoTest {

    public static Feed feed;
    public static FeedDto feedDto;
    static {
        FeedDtoConverter converter = new FeedDtoConverter();

        feed = new Feed();
        feed.setFeedId("feedId-Test");
        feed.setFeedType("feedType-Test");
        Member owner = new Member();
        owner.setMemberId("ownerId-Test");
        owner.setName("ownerName-Test");
        feed.setOwner(owner);
        Record record = new Record();
        record.setRecordId("recordId-Test");
        feed.setRecord(record);
        feed.setMusicName("musicName-Test");
        feed.setMusicianName("musicianName-Test");
        feed.setViewCount(100);
        feed.setCreatedAt(LocalDateTime.now());
        feed.setUpdatedAt(LocalDateTime.now());
        feed.setDescription("description-Test");

        feedDto = converter.convertToDto(feed);
    }

    @Test
    public void testFeedDto() {
        FeedDtoConverter converter = new FeedDtoConverter();

        FeedDto feedDto = converter.convertToDto(feed);
        log.info(feedDto.toString());

        // Assertions
        assert(feedDto.getFeedId().equals("feedId-Test"));
        assert(feedDto.getFeedType().equals("feedType-Test"));
        assert(feedDto.getOwnerId().equals("ownerId-Test"));
        assert(feedDto.getOwnerName().equals("ownerName-Test"));
        assert(feedDto.getRecordId().equals("recordId-Test"));
        assert(feedDto.getMusicName().equals("musicName-Test"));
        assert(feedDto.getMusicianName().equals("musicianName-Test"));
        assert(feedDto.getViewCount() == 100);
        assert(feedDto.getCreatedAt().equals(feed.getCreatedAt()));
        assert(feedDto.getUpdatedAt().equals(feed.getUpdatedAt()));
        assert(feedDto.getDescription().equals("description-Test"));

        List<Feed> feedList = Arrays.asList(feed, new Feed());
        List<FeedDto> feedDtoList = converter.convertList(feedList);
        log.info(feedDtoList.toString());

        assert(feedDtoList.size() == 2);
    }
}
