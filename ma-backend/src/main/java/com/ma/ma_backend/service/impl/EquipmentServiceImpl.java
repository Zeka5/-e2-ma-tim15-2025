package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.*;
import com.ma.ma_backend.exception.InvalidRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.repository.*;
import com.ma.ma_backend.service.intr.EquipmentService;
import com.ma.ma_backend.service.intr.UserService;
import com.ma.ma_backend.util.ShopPriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
    private final UserPotionRepository userPotionRepository;
    private final UserClothingRepository userClothingRepository;
    private final UserWeaponRepository userWeaponRepository;
    private final UserGameStatsRepository userGameStatsRepository;
    private final UserService userService;
    private final ShopPriceCalculator priceCalculator;

    @Override
    @Transactional(readOnly = true)
    public UserEquipmentDto getAllEquipment() {
        return UserEquipmentDto.builder()
                .potions(getPotions())
                .clothing(getClothing())
                .weapons(getWeapons())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPotionDto> getPotions() {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        List<UserPotion> potions = userPotionRepository.findByUserGameStats(stats);

        return potions.stream()
                .map(this::mapPotionToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserClothingDto> getClothing() {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        List<UserClothing> clothing = userClothingRepository.findByUserGameStatsId(stats.getId());

        return clothing.stream()
                .map(this::mapClothingToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserWeaponDto> getWeapons() {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        List<UserWeapon> weapons = userWeaponRepository.findByUserGameStatsId(stats.getId());

        return weapons.stream()
                .map(this::mapWeaponToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void activatePotion(Long userPotionId) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        UserPotion potion = userPotionRepository.findById(userPotionId)
                .orElseThrow(() -> new NotFoundException("Potion not found"));

        if (!potion.getUserGameStats().getId().equals(stats.getId())) {
            throw new InvalidRequestException("This potion does not belong to you");
        }

        if (potion.getQuantity() <= 0) {
            throw new InvalidRequestException("No potions available to activate");
        }

        potion.setIsActivated(true);
        userPotionRepository.save(potion);
    }

    @Override
    @Transactional
    public void deactivatePotion(Long userPotionId) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        UserPotion potion = userPotionRepository.findById(userPotionId)
                .orElseThrow(() -> new NotFoundException("Potion not found"));

        if (!potion.getUserGameStats().getId().equals(stats.getId())) {
            throw new InvalidRequestException("This potion does not belong to you");
        }

        potion.setIsActivated(false);
        userPotionRepository.save(potion);
    }

    @Override
    @Transactional
    public void activateClothing(Long userClothingId) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        UserClothing clothing = userClothingRepository.findById(userClothingId)
                .orElseThrow(() -> new NotFoundException("Clothing not found"));

        if (!clothing.getUserGameStats().getId().equals(stats.getId())) {
            throw new InvalidRequestException("This clothing does not belong to you");
        }

        if (clothing.getBattlesRemaining() <= 0) {
            throw new InvalidRequestException("This clothing has no battles remaining");
        }

        // Just activate - bonus will be calculated during battle
        clothing.setIsActive(true);
        userClothingRepository.save(clothing);
    }

    @Override
    @Transactional
    public void deactivateClothing(Long userClothingId) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        UserClothing clothing = userClothingRepository.findById(userClothingId)
                .orElseThrow(() -> new NotFoundException("Clothing not found"));

        if (!clothing.getUserGameStats().getId().equals(stats.getId())) {
            throw new InvalidRequestException("This clothing does not belong to you");
        }

        clothing.setIsActive(false);
        userClothingRepository.save(clothing);
    }

    // Mapping methods

    private UserPotionDto mapPotionToDto(UserPotion potion) {
        PotionTemplate template = potion.getPotionTemplate();
        return UserPotionDto.builder()
                .id(potion.getId())
                .potionTemplateId(template.getId())
                .name(template.getName())
                .powerBonus(template.getPowerBonus())
                .isPermanent(template.getIsPermanent())
                .quantity(potion.getQuantity())
                .isActivated(potion.getIsActivated())
                .acquiredAt(potion.getAcquiredAt())
                .description(template.getDescription())
                .iconUrl(template.getIconUrl())
                .build();
    }

    private UserClothingDto mapClothingToDto(UserClothing clothing) {
        ClothingTemplate template = clothing.getClothingTemplate();
        return UserClothingDto.builder()
                .id(clothing.getId())
                .clothingTemplateId(template.getId())
                .name(template.getName())
                .type(template.getClothingType().name())
                .accumulatedBonus(clothing.getAccumulatedBonus())
                .battlesRemaining(clothing.getBattlesRemaining())
                .isActive(clothing.getIsActive())
                .acquiredAt(clothing.getAcquiredAt())
                .description(template.getDescription())
                .iconUrl(template.getIconUrl())
                .build();
    }

    private UserWeaponDto mapWeaponToDto(UserWeapon weapon) {
        WeaponTemplate template = weapon.getWeaponTemplate();
        return UserWeaponDto.builder()
                .id(weapon.getId())
                .weaponTemplateId(template.getId())
                .name(template.getName())
                .type(template.getWeaponType().name())
                .currentBonusPercentage(weapon.getCurrentBonusPercentage())
                .upgradeLevel(weapon.getUpgradeLevel())
                .duplicateCount(weapon.getDuplicateCount())
                .acquiredAt(weapon.getAcquiredAt())
                .description(template.getDescription())
                .iconUrl(template.getIconUrl())
                .build();
    }

    @Override
    @Transactional
    public void upgradeWeapon(Long userWeaponId) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        UserWeapon weapon = userWeaponRepository.findById(userWeaponId)
                .orElseThrow(() -> new NotFoundException("Weapon not found"));

        if (!weapon.getUserGameStats().getId().equals(stats.getId())) {
            throw new InvalidRequestException("This weapon does not belong to you");
        }

        // Calculate upgrade cost (60% of boss reward from previous level)
        WeaponTemplate template = weapon.getWeaponTemplate();
        Integer upgradeCost = priceCalculator.calculatePrice(stats.getLevel(), template.getUpgradePriceMultiplier());

        if (stats.getCoins() < upgradeCost) {
            throw new InvalidRequestException("Not enough coins. Need: " + upgradeCost + ", Have: " + stats.getCoins());
        }

        // Deduct coins
        stats.setCoins(stats.getCoins() - upgradeCost);
        userGameStatsRepository.save(stats);

        // Upgrade weapon: increase bonus by 0.01% and increment upgrade level
        weapon.setCurrentBonusPercentage(weapon.getCurrentBonusPercentage() + 0.01);
        weapon.setUpgradeLevel(weapon.getUpgradeLevel() + 1);
        userWeaponRepository.save(weapon);
    }
}
