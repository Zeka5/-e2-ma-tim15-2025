package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entitet za praćenje dana kada je korisnik poslao poruku u savezu tokom specijalne misije
 */
@Data
@Entity
@Table(name = "guild_message_dates")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildMessageDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_boss_mission_progress_id", nullable = false)
    private GuildBossMissionProgress missionProgress;

    @Column(name = "message_date", nullable = false)
    private LocalDate messageDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
