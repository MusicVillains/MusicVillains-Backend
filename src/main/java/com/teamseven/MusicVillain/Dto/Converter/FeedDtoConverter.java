package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Feed.Feed;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FeedDtoConverter implements Converter<Feed, FeedDto> {
    @Override
    public FeedDto convertToDto(Feed source) {
        FeedDto feedDto = new FeedDto();
        feedDto.setFeedId(source.getFeedId());
        feedDto.setFeedType(source.getFeedType());
        if(source.getOwner() != null) {
            feedDto.setOwnerId(source.getOwner().getMemberId());
            feedDto.setOwnerName(source.getOwner().getName());
        }
        if(source.getRecord() != null) {
            feedDto.setRecordId(source.getRecord().getRecordId());
        }
        feedDto.setMusicName(source.getMusicName());
        feedDto.setMusicianName(source.getMusicianName());
        feedDto.setViewCount(source.getViewCount());
        feedDto.setCreatedAt(source.getCreatedAt());
        feedDto.setUpdatedAt(source.getUpdatedAt());
        feedDto.setDescription(source.getDescription());
        return feedDto;
    }

    @Override
    public List<FeedDto> convertList(List<Feed> sourceList) {
        return sourceList.stream()
                .map(this::convertToDto) // 각 Feed 객체를 FeedDto로 변환
                .collect(Collectors.toList()); // List<FeedDto>로 모아 반환
    }
}
