package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user_game_stats")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGameStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "level")
    private Integer level = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "title")
    private Title title = Title.NOVICE;

    @Column(name = "power_points")
    private Integer powerPoints = 100;

    @Column(name = "experience_points")
    private Integer experiencePoints = 0;

    @Column(name = "coins")
    private Integer coins = 0;

    @ManyToMany
    @JoinTable(
        name = "user_badges",
        joinColumns = @JoinColumn(name = "user_game_stats_id"),
        inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private List<Badge> earnedBadges = new ArrayList<>();

    @Column(name = "qr_code")
    private String qrCode;
}
