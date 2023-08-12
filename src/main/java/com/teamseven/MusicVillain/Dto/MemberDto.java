package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class MemberDto {
    public String memberId; // PK, uuid.toString()
    public String userId;
    public String name;
    public String email;
    public String role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime updatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime lastLoginAt;
    public String providerType;
}
