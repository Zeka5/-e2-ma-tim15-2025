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
public class UserEquipmentDto {
    private List<UserPotionDto> potions;
    private List<UserClothingDto> clothing;
    private List<UserWeaponDto> weapons;
}
