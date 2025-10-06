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
public class UserWeaponDto {
    private Long id;
    private Long weaponTemplateId;
    private String name;
    private String type;
    private Double currentBonusPercentage;
    private Integer upgradeLevel;
    private Integer duplicateCount;
    private LocalDateTime acquiredAt;
    private String description;
    private String iconUrl;
}
