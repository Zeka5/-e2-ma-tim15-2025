package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.ClothingTemplateDto;
import com.ma.ma_backend.dto.PotionTemplateDto;

import java.util.List;

public interface ShopService {
    List<PotionTemplateDto> getAvailablePotions();
    List<ClothingTemplateDto> getAvailableClothing();
    void purchasePotion(Long templateId, Integer quantity);
    void purchaseClothing(Long templateId);
}
