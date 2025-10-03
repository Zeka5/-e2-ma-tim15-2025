package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_potions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_stats_id", nullable = false)
    private UserGameStats userGameStats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potion_template_id", nullable = false)
    private PotionTemplate potionTemplate;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "is_activated")
    private Boolean isActivated = false;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt = LocalDateTime.now();
}
