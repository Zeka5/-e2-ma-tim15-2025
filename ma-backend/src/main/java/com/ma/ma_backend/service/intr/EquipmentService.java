package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.UserClothingDto;
import com.ma.ma_backend.dto.UserEquipmentDto;
import com.ma.ma_backend.dto.UserPotionDto;
import com.ma.ma_backend.dto.UserWeaponDto;

import java.util.List;

public interface EquipmentService {
    UserEquipmentDto getAllEquipment();
    List<UserPotionDto> getPotions();
    List<UserClothingDto> getClothing();
    List<UserWeaponDto> getWeapons();

    void activatePotion(Long userPotionId);
    void deactivatePotion(Long userPotionId);

    void activateClothing(Long userClothingId);
    void deactivateClothing(Long userClothingId);
}
