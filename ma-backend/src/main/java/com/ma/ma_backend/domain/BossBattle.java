package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "boss_battles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BossBattle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_stats_id", nullable = false)
    private UserGameStats userGameStats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false)
    private Boss boss;

    @Column(name = "current_hp", nullable = false)
    private Integer currentHp;

    @Column(name = "attacks_used", nullable = false)
    private Integer attacksUsed = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BattleStatus status = BattleStatus.IN_PROGRESS;

    @Column(name = "coins_earned")
    private Integer coinsEarned = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_earned_weapon_id")
    private WeaponTemplate equipmentEarnedWeapon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_earned_clothing_id")
    private ClothingTemplate equipmentEarnedClothing;

    @Column(name = "user_pp_at_battle", nullable = false)
    private Integer userPpAtBattle;

    @Column(name = "success_rate_at_battle", nullable = false)
    private Double successRateAtBattle;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
