package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Feed.Feed;

import java.util.List;
import java.util.stream.Collectors;

public class FeedDtoConverter implements DtoConverter<Feed, FeedDto> {
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
        feedDto.setRecordRawData(source.getRecord().getRecordRawData());
        if(source.getInteractionList() != null)
            /* WARN 테스트 코드에서 Feed.getInteractionList()가 null 인 경우가 있어서 확인될 때까지 임시로 null 체크 */
            feedDto.setInteractionCount(source.getInteractionList().size());
            else feedDto.setInteractionCount(0);

        return feedDto;
    }

    @Override
    public List<FeedDto> convertToDtoList(List<Feed> entityList) {
        return entityList.stream()
                .map(this::convertToDto) // 각 Feed 객체를 FeedDto로 변환
                .collect(Collectors.toList()); // List<FeedDto>로 모아 반환
    }

}
