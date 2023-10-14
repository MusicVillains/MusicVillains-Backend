package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Dto.InteractionProps;
import com.teamseven.MusicVillain.Feed.Feed;
import com.teamseven.MusicVillain.Interaction.Interaction;

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


        /* 로그인 하지 않은 사용자 또는 Member가 아닌 사용자는 interactionProps를 반환할 때,
        Interaction을 수행하지 않은 상태로 반환한다. */

        InteractionProps interactionProps = new InteractionProps();
        interactionProps.setInteracted(false);

        feedDto.setInteractionProps(interactionProps);
        return feedDto;
    }
    /**
     * @see com.teamseven.MusicVillain.Feed.FeedService#getAllFeedsForMember(String)
     *
     * @apiNote
     * 로그인한 사용자가 Feed 조회를 요청했을 때 사용된다. 각 피드에 대해 사용자가 해당 피드에<br>
     * Interaction을 수행했는지 여부를 확인하여 InteractionProps 설정하고 반환한다.<br>
     * 수행한 피드의 경우 -  "interactionProps": { content : "박수", backgroundColor :"#EAED70" }<br>
     * 수행하지 않은 피드의 경우 - "interactionProps": { content : "👏", border : "2px solid #651fff" }<br?
     * @param source FeedDto로 변환하고자하는 Feed 객체
     * @param memberId 요청하는 회원의 식별자
     * @return 피드 리스트를 DTO로 변환한 결과
     */
    public FeedDto convertToDtoForMember(Feed source, String memberId){
        FeedDto feedDto = this.convertToDto(source);
        // check if member has interacted with this feed

        InteractionProps interactionProps = new InteractionProps();
        interactionProps.setInteracted(false);
        feedDto.setInteractionProps(interactionProps);

        for (Interaction interaction : source.getInteractionList()){
            // if member has interacted with this feed
            if (interaction.getInteractionMember().getMemberId().equals(memberId)){
                interactionProps.setInteracted(true);
                feedDto.setInteractionProps(interactionProps);
                return feedDto;
            }
        }

        return feedDto;
    }

    @Override
    public List<FeedDto> convertToDtoList(List<Feed> entityList) {
        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * @see com.teamseven.MusicVillain.Feed.FeedService#getAllFeedsForMember(String)
     * @see com.teamseven.MusicVillain.Dto.Converter.FeedDtoConverter#convertToDtoForMember(Feed, String)
     *
     * @apiNote
     * 로그인한 사용자가 Feed 조회를 요청했을 때 사용된다.
     * 각 피드에 대해 사용자가 해당 피드에 Interaction을 수행했는지 여부를 확인하여 InteractionProps 설정하고 반환한다.<br>
     * 수행한 피드의 경우 -  "interactionProps": { content : "박수", backgroundColor :"#EAED70" }<br>
     * 수행하지 않은 피드의 경우 - "interactionProps": { content : "👏", border : "2px solid #651fff" }<br?
     *
     * @param entityList FeedDto List로 변환하고자하는 Feed List
     * @param memberId 요청하는 회원의 식별자
     * @return 피드 리스트를 DTO 리스트로 변환한 결과
     */
    public List<FeedDto> convertToDtoListForMember(List<Feed> entityList, String memberId) {
        return entityList.stream()
                .map(feed -> this.convertToDtoForMember(feed, memberId))
                .collect(Collectors.toList());
    }

}
