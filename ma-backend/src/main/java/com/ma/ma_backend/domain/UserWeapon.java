package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_weapons")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWeapon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_stats_id", nullable = false)
    private UserGameStats userGameStats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weapon_template_id", nullable = false)
    private WeaponTemplate weaponTemplate;

    @Column(name = "current_bonus_percentage")
    private Double currentBonusPercentage;

    @Column(name = "upgrade_level")
    private Integer upgradeLevel = 0;

    @Column(name = "duplicate_count")
    private Integer duplicateCount = 0;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt = LocalDateTime.now();
}
