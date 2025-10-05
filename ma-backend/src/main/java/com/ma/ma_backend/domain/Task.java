package com.ma.ma_backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tasks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskDifficulty difficulty;

    @Column(name = "difficulty_xp", nullable = false)
    private Integer difficultyXp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskImportance importance;

    @Column(name = "importance_xp", nullable = false)
    private Integer importanceXp;

    @Column(name = "total_xp", nullable = false)
    private Integer totalXp;

    @Column(name = "is_repeating", nullable = false)
    private Boolean isRepeating;

    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_unit")
    private RepeatUnit recurrenceUnit;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isRepeating == null) {
            isRepeating = false;
        }
        calculateXpValues();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateXpValues();
    }

    private void calculateXpValues() {
        if (difficulty != null) {
            difficultyXp = difficulty.getXp();
        }
        if (importance != null) {
            importanceXp = importance.getXp();
        }
        if (difficultyXp != null && importanceXp != null) {
            totalXp = difficultyXp + importanceXp;
        }
    }
}
