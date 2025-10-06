package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttackRequest {
    private Long battleId;
    private List<Long> activeEquipmentIds; // IDs of UserWeapon or UserClothing being used
}
