package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildBossMissionProgressDto {
    private Long id;
    private Long userId;
    private String username;
    private Integer shopPurchases;
    private Integer bossHits;
    private Integer easyTasksCompleted;
    private Integer hardTasksCompleted;
    private Boolean noUncompletedTasks;
    private Integer daysWithMessages;
    private Integer totalDamageDealt;
    private Integer totalTasksCompleted;
    private String badge;
    private String badgeTitle;
}
