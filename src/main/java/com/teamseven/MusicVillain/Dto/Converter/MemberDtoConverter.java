package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Member.Member;

import java.util.List;
import java.util.stream.Collectors;

public class MemberDtoConverter implements DtoConverter<Member, MemberDto> {

    @Override
    public MemberDto convertToDto(Member memberEntity) {
        if(memberEntity == null) return null;

        MemberDto memberDto = MemberDto.builder()
                .memberId(memberEntity.getMemberId())
                .userId(memberEntity.getUserId())
                .userInfo(memberEntity.getUserInfo())
                .name(memberEntity.getName())
                .email(memberEntity.getEmail())
                .createdAt(memberEntity.getCreatedAt())
                .updatedAt(memberEntity.getUpdatedAt())
                .role(memberEntity.getRole())
                .lastLoginAt(memberEntity.getLastLoginAt())
                .providerType(memberEntity.getProviderType())
                .build();
        return memberDto;
    }

    @Override
    public List<MemberDto> convertToDtoList(List<Member> entityList) {
        if(entityList.isEmpty()) return null;

        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
