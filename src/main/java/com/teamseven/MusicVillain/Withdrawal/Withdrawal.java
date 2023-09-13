package com.teamseven.MusicVillain.Withdrawal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "withdrawal_table")
public class Withdrawal {
    @Id
    @Column(name = "withdrawal_id")
    private String withdrawalId; // PK, uuid.toString()

    @Column(name = "reason", nullable = false)
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public Withdrawal(String withdrawalId, String reason, LocalDateTime createdAt) {
        this.withdrawalId = withdrawalId;
        this.reason = reason;
        this.createdAt = createdAt;
    }
}
