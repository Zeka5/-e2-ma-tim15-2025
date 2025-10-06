package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BossBattleDto {
    private Long id;
    private BossDto boss;
    private Integer currentHp;
    private Integer attacksUsed;
    private String status;
    private Integer coinsEarned;
    private String equipmentEarnedType; // "WEAPON" or "CLOTHING"
    private String equipmentEarnedName;
    private Integer userPpAtBattle;
    private Double successRateAtBattle;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
