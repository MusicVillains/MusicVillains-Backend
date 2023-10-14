package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.teamseven.MusicVillain.Member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class MemberDto implements DataTransferObject<Member> {
    @Schema(example = "uuid.toString()")
    public final String memberId; // PK, uuid.toString(), Never change after initialized

    @Schema(example = "12345")
    public final String userId; // related to OAuth2 provider, Never change after initialized

    @JsonIgnore
    public String userInfo;

    @Schema(example = "홍길동")
    public String name;

    @Schema(example = "member@example.com")
    @JsonIgnore
    public String email;

    @Schema(example = "1")
    public String role;

    @Schema(example = "2021-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createdAt;

    @Schema(example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime updatedAt;

    @Schema(example = "2023-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime lastLoginAt;

    @Schema(example = "1")
    public String providerType;

}
