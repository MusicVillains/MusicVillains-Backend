package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Feed.Feed;

import java.util.List;
import java.util.stream.Collectors;

public class FeedDtoConverter implements DtoConverter<Feed, FeedDto> {
    @Override
    public FeedDto convertToDto(Feed source) {
        FeedDto feedDto = new FeedDto();

        if(source.getFeedId() != null)
            feedDto.setFeedId(source.getFeedId());

        if(source.getFeedType() != null)
            feedDto.setFeedType(source.getFeedType());

        if(source.getOwner() != null) {
            feedDto.setOwnerId(source.getOwner().getMemberId());
            feedDto.setOwnerName(source.getOwner().getName());
        }

        if(source.getRecord() != null) {
            feedDto.setRecordId(source.getRecord().getRecordId());
            feedDto.setRecordRawData(source.getRecord().getRecordRawData());
        }

        if(source.getMusicName() != null)
            feedDto.setMusicName(source.getMusicName());

        if(source.getMusicianName() != null)
            feedDto.setMusicianName(source.getMusicianName());

        feedDto.setViewCount(source.getViewCount());

        if(source.getCreatedAt() != null)
            feedDto.setCreatedAt(source.getCreatedAt());

        if(source.getUpdatedAt() != null)
            feedDto.setUpdatedAt(source.getUpdatedAt());
        if(source.getDescription() != null)
            feedDto.setDescription(source.getDescription());

        if(source.getInteractionList() != null)
            feedDto.setInteractionCount(source.getInteractionList().size());
            else feedDto.setInteractionCount(0);

        return feedDto;
    }

    @Override
    public List<FeedDto> convertToDtoList(List<Feed> entityList) {
        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
