package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuildBossBattleDto {
    private Long id;
    private Long guildId;
    private String guildName;
    private String bossName;
    private Integer maxHp;
    private Integer currentHp;
    private Integer memberCount;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endsAt;
    private LocalDateTime completedAt;
    private Double progressPercentage;
}
