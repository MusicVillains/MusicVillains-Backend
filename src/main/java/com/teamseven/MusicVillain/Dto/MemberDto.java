package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class MemberDto {
    @Schema(example = "uuid.toString()")
    public String memberId; // PK, uuid.toString()

    @Schema(example = "12345")
    public String userId;

    @Schema(example = "홍길동")
    public String name;

    @Schema(example = "member@example.com")
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
