package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_clothing")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserClothing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_stats_id", nullable = false)
    private UserGameStats userGameStats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothing_template_id", nullable = false)
    private ClothingTemplate clothingTemplate;

    @Column(name = "accumulated_bonus")
    private Integer accumulatedBonus;

    @Column(name = "battles_remaining")
    private Integer battlesRemaining = 2;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "acquired_at")
    private LocalDateTime acquiredAt = LocalDateTime.now();
}
