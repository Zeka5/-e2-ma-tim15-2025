package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleRewardsDto {
    private Integer coinsEarned;
    private String equipmentType; // "WEAPON", "CLOTHING", or null
    private String equipmentName;
    private Long equipmentId;
}
