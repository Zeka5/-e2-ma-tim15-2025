package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "guild_boss_battles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildBossBattle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @Column(name = "boss_name", nullable = false)
    private String bossName;

    @Column(name = "max_hp", nullable = false)
    private Integer maxHp;

    @Column(name = "current_hp", nullable = false)
    private Integer currentHp;

    @Column(name = "member_count", nullable = false)
    private Integer memberCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BattleStatus status = BattleStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = BattleStatus.IN_PROGRESS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
