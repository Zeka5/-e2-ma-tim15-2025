package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "guild_boss_mission_progress")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildBossMissionProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_boss_battle_id", nullable = false)
    private GuildBossBattle guildBossBattle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Kupovina u prodavnici (max 5) - 2 HP
    @Column(name = "shop_purchases", nullable = false)
    private Integer shopPurchases = 0;

    // Uspešan udarac u regularnoj borbi sa bosom (max 10) - 2 HP
    @Column(name = "boss_hits", nullable = false)
    private Integer bossHits = 0;

    // Rešavanje veoma lakog, lakog, normalnog ili važnog zadatka (max 10) - 1 HP (lak i normalan = 2)
    @Column(name = "easy_tasks_completed", nullable = false)
    private Integer easyTasksCompleted = 0;

    // Rešavanje ostalih zadataka (max 6) - 4 HP
    @Column(name = "hard_tasks_completed", nullable = false)
    private Integer hardTasksCompleted = 0;

    // Bez nerešenih zadataka tokom trajanja (boolean flag)
    @Column(name = "no_uncompleted_tasks", nullable = false)
    private Boolean noUncompletedTasks = true;

    // Poslata poruka u savezu (računa se na nivou dana) - za svaki dan 4 HP
    @Column(name = "days_with_messages", nullable = false)
    private Integer daysWithMessages = 0;

    // Ukupan damage koji je korisnik naneo bosu
    @Column(name = "total_damage_dealt", nullable = false)
    private Integer totalDamageDealt = 0;

    // Ukupan broj zadataka koji su uspešno rešeni
    @Column(name = "total_tasks_completed", nullable = false)
    private Integer totalTasksCompleted = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper metoda za dodavanje damage-a
    public void addDamage(int damage) {
        this.totalDamageDealt += damage;
    }

    // Helper metoda za proveru da li je dostignut limit za shop purchases
    public boolean canAddShopPurchase() {
        return shopPurchases < 5;
    }

    // Helper metoda za proveru da li je dostignut limit za boss hits
    public boolean canAddBossHit() {
        return bossHits < 10;
    }

    // Helper metoda za proveru da li je dostignut limit za easy tasks
    public boolean canAddEasyTask() {
        return easyTasksCompleted < 10;
    }

    // Helper metoda za proveru da li je dostignut limit za hard tasks
    public boolean canAddHardTask() {
        return hardTasksCompleted < 6;
    }
}
