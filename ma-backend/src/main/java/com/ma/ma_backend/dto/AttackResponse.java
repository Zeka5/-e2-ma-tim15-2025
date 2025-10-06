package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttackResponse {
    private Boolean hit; // Whether the attack was successful
    private Integer damageDealt;
    private Integer bossCurrentHp;
    private Integer attacksRemaining;
    private Boolean battleComplete;
    private String battleResult; // "WON", "LOST", or null if still in progress
    private BattleRewardsDto rewards;
}
