package com.teamseven.MusicVillain.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class MemberDto implements DataTransferObject {
    public final String memberId; // PK, uuid.toString(), Never change after initialized
    public final String userId; // related to OAuth2 provider, Never change after initialized
    @JsonIgnore
    public String userInfo;
    public String name;
    @JsonIgnore
    public String email;
    public String role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime updatedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime lastLoginAt;
    public String providerType;

    @Override
    public Object toDto(Object o) {
        return null;
    }

    @Override
    public List toDtoList(List list) {
        return null;
    }
}
