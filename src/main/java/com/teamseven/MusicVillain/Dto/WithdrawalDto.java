package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawalDto implements DataTransferObject{
    @Schema(example = "uuid.toString()")
    public final String withdrawalId;

    @Schema(example = "uuid.toString()")
    public final String memberId;

    @Schema(example = "탈퇴 사유")
    public String reason;

    @Schema(example = "2023-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createdAt;

    @Override
    public Object toDto(Object o) {
        return null;
    }

    @Override
    public List toDtoList(List list) {
        return null;
    }
}
