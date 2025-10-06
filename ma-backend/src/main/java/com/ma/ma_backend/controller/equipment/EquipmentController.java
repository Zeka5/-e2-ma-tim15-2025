package com.ma.ma_backend.controller.equipment;

import com.ma.ma_backend.dto.UserClothingDto;
import com.ma.ma_backend.dto.UserEquipmentDto;
import com.ma.ma_backend.dto.UserPotionDto;
import com.ma.ma_backend.dto.UserWeaponDto;
import com.ma.ma_backend.service.intr.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<UserEquipmentDto> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @GetMapping("/potions")
    public ResponseEntity<List<UserPotionDto>> getPotions() {
        return ResponseEntity.ok(equipmentService.getPotions());
    }

    @GetMapping("/clothing")
    public ResponseEntity<List<UserClothingDto>> getClothing() {
        return ResponseEntity.ok(equipmentService.getClothing());
    }

    @GetMapping("/weapons")
    public ResponseEntity<List<UserWeaponDto>> getWeapons() {
        return ResponseEntity.ok(equipmentService.getWeapons());
    }

    @PostMapping("/potions/{potionId}/activate")
    public ResponseEntity<Void> activatePotion(@PathVariable Long potionId) {
        equipmentService.activatePotion(potionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/potions/{potionId}/deactivate")
    public ResponseEntity<Void> deactivatePotion(@PathVariable Long potionId) {
        equipmentService.deactivatePotion(potionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clothing/{clothingId}/activate")
    public ResponseEntity<Void> activateClothing(@PathVariable Long clothingId) {
        equipmentService.activateClothing(clothingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clothing/{clothingId}/deactivate")
    public ResponseEntity<Void> deactivateClothing(@PathVariable Long clothingId) {
        equipmentService.deactivateClothing(clothingId);
        return ResponseEntity.ok().build();
    }
}
