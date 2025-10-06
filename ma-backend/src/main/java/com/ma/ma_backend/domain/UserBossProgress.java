package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks which bosses are pending/completed for each user.
 * When a user completes a level without defeating the boss for that level,
 * the boss becomes pending and will appear before the next boss.
 */
@Data
@Entity
@Table(name = "user_boss_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_game_stats_id", "boss_id"}))
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBossProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_stats_id", nullable = false)
    private UserGameStats userGameStats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false)
    private Boss boss;

    @Column(nullable = false)
    private Boolean defeated = false;

    @Column(name = "defeated_at")
    private LocalDateTime defeatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
