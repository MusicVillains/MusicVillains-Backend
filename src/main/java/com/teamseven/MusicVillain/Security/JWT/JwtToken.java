package com.teamseven.MusicVillain.Security.JWT;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "token_table")
public class JwtToken {
    @Id
    @Column(name = "token_id")
    public String tokenId;
    @Column(name = "owner_id")
    public String ownerId;
    @Column(name = "type")
    public String type;
    @Lob
    @Column(name = "value")
    public String value;
    @Column(name = "expired_at")
    public LocalDateTime expiredAt;

}
