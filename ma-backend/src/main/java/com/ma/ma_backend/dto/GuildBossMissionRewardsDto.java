package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildBossMissionRewardsDto {
    private Integer coinsEarned;
    private String potionName;
    private Long potionId;
    private String clothingName;
    private Long clothingId;
    private String badge;
    private String badgeTitle;
    private Integer tasksCompleted;
}
