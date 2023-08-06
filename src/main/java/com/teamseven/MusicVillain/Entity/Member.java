package com.teamseven.MusicVillain.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "member_table")
public class Member {
    @Id
    @Column(name = "member_id")
    public String memberId; // PK, uuid.toString()
    @Column(name = "user_id")
    public String userId;
    @Column(name = "user_info")
    public String userInfo;
    @Column(name = "name")
    public String name;
    @Column(name ="email")
    public String email;
    @Column(name = "role")
    public String role;
    @Column(name = "created_at")
    public LocalDateTime createdAt;
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
    @Column(name = "last_login_at")
    public LocalDateTime lastLoginAt;

    @Builder
    public Member(String memberId, String userId, String userInfo, String name, String email,
                  String role, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
        this.memberId = memberId;
        this.userId = userId;
        this.userInfo = userInfo;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
    }
}
