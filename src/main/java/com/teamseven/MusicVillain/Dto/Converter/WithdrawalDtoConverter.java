package com.teamseven.MusicVillain.Dto.Converter;

import com.teamseven.MusicVillain.Dto.WithdrawalDto;
import com.teamseven.MusicVillain.Withdrawal.Withdrawal;

import java.util.List;
import java.util.stream.Collectors;

public class WithdrawalDtoConverter implements DtoConverter<Withdrawal, WithdrawalDto> {

    @Override
    public WithdrawalDto convertToDto(Withdrawal withdrawalEntity) {
        if(withdrawalEntity == null) return null;

        WithdrawalDto withdrawalDto = WithdrawalDto.builder()
                .withdrawalId(withdrawalEntity.getWithdrawalId())
                .reason(withdrawalEntity.getReason())
                .createdAt(withdrawalEntity.getCreatedAt())
                .build();
        return withdrawalDto;
    }

    @Override
    public List<WithdrawalDto> convertToDtoList(List<Withdrawal> entityList) {
        if(entityList.isEmpty()) return null;

        return entityList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
