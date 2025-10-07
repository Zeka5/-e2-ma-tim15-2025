package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildBossMissionSummaryDto {
    private GuildBossBattleDto battle;
    private List<GuildBossMissionProgressDto> memberProgress;
    private Integer totalDamageDealt;
    private Boolean isActive;
}
