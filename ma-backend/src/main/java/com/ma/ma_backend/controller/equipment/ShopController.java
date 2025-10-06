package com.ma.ma_backend.controller.equipment;

import com.ma.ma_backend.dto.ClothingTemplateDto;
import com.ma.ma_backend.dto.PotionTemplateDto;
import com.ma.ma_backend.dto.PurchaseItemRequest;
import com.ma.ma_backend.service.intr.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping("/potions")
    public ResponseEntity<List<PotionTemplateDto>> getAvailablePotions() {
        return ResponseEntity.ok(shopService.getAvailablePotions());
    }

    @GetMapping("/clothing")
    public ResponseEntity<List<ClothingTemplateDto>> getAvailableClothing() {
        return ResponseEntity.ok(shopService.getAvailableClothing());
    }

    @PostMapping("/potions/purchase")
    public ResponseEntity<Void> purchasePotion(@RequestBody PurchaseItemRequest request) {
        shopService.purchasePotion(request.getTemplateId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clothing/purchase/{templateId}")
    public ResponseEntity<Void> purchaseClothing(@PathVariable Long templateId) {
        shopService.purchaseClothing(templateId);
        return ResponseEntity.ok().build();
    }
}
