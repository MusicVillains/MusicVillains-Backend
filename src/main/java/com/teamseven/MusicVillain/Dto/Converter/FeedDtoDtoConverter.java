package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Feed.Feed;

import java.util.List;
import java.util.stream.Collectors;

public class FeedDtoDtoConverter implements DtoConverter<Feed, FeedDto> {
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
<<<<<<< HEAD:src/main/java/com/teamseven/MusicVillain/Dto/Converter/FeedDtoConverter.java
    public List<FeedDto> convertList(List<Feed> sourceList) {
        return sourceList.stream()
=======
    public List<FeedDto> convertToDtoList(List<Feed> entityList) {
        return entityList.stream()
>>>>>>> f91bbef1dd0cac2589df7ca7c1633fd8ba5320cc:src/main/java/com/teamseven/MusicVillain/Dto/Converter/FeedDtoDtoConverter.java
                .map(this::convertToDto) // 각 Feed 객체를 FeedDto로 변환
                .collect(Collectors.toList()); // List<FeedDto>로 모아 반환
    }

}
