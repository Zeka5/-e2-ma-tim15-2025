package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.ClothingTemplateDto;
import com.ma.ma_backend.dto.PotionTemplateDto;
import com.ma.ma_backend.exception.InvalidRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.repository.*;
import com.ma.ma_backend.service.intr.ShopService;
import com.ma.ma_backend.service.intr.UserService;
import com.ma.ma_backend.util.ShopPriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final PotionTemplateRepository potionTemplateRepository;
    private final ClothingTemplateRepository clothingTemplateRepository;
    private final UserPotionRepository userPotionRepository;
    private final UserClothingRepository userClothingRepository;
    private final UserGameStatsRepository userGameStatsRepository;
    private final UserService userService;
    private final ShopPriceCalculator priceCalculator;

    @Override
    @Transactional(readOnly = true)
    public List<PotionTemplateDto> getAvailablePotions() {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        List<PotionTemplate> templates = potionTemplateRepository.findAll();

        return templates.stream()
                .map(template -> {
                    Integer price = priceCalculator.calculatePrice(stats.getLevel(), template.getPriceMultiplier());
                    return mapPotionToDto(template, price);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClothingTemplateDto> getAvailableClothing() {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        List<ClothingTemplate> templates = clothingTemplateRepository.findAll();

        return templates.stream()
                .map(template -> {
                    Integer price = priceCalculator.calculatePrice(stats.getLevel(), template.getPriceMultiplier());
                    return mapClothingToDto(template, price);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void purchasePotion(Long templateId, Integer quantity) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        PotionTemplate template = potionTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Potion template not found"));

        Integer price = priceCalculator.calculatePrice(stats.getLevel(), template.getPriceMultiplier());
        Integer totalCost = price * quantity;

        if (stats.getCoins() < totalCost) {
            throw new InvalidRequestException("Not enough coins. Need: " + totalCost + ", Have: " + stats.getCoins());
        }

        // Deduct coins
        stats.setCoins(stats.getCoins() - totalCost);
        userGameStatsRepository.save(stats);

        // Add potion to inventory (or increase quantity if already exists)
        Optional<UserPotion> existing = userPotionRepository.findByUserGameStatsAndPotionTemplate(stats, template);

        if (existing.isPresent()) {
            UserPotion userPotion = existing.get();
            userPotion.setQuantity(userPotion.getQuantity() + quantity);
            userPotionRepository.save(userPotion);
        } else {
            UserPotion userPotion = UserPotion.builder()
                    .userGameStats(stats)
                    .potionTemplate(template)
                    .quantity(quantity)
                    .isActivated(false)
                    .build();
            userPotionRepository.save(userPotion);
        }
    }

    @Override
    @Transactional
    public void purchaseClothing(Long templateId) {
        User currentUser = userService.getLogedInUser();
        UserGameStats stats = currentUser.getGameStats();

        ClothingTemplate template = clothingTemplateRepository.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Clothing template not found"));

        Integer price = priceCalculator.calculatePrice(stats.getLevel(), template.getPriceMultiplier());

        if (stats.getCoins() < price) {
            throw new InvalidRequestException("Not enough coins. Need: " + price + ", Have: " + stats.getCoins());
        }

        // Deduct coins
        stats.setCoins(stats.getCoins() - price);
        userGameStatsRepository.save(stats);

        // Add clothing to inventory
        UserClothing userClothing = UserClothing.builder()
                .userGameStats(stats)
                .clothingTemplate(template)
                .accumulatedBonus(template.getBonusPercentage())
                .battlesRemaining(2)
                .isActive(false)
                .build();
        userClothingRepository.save(userClothing);
    }

    private PotionTemplateDto mapPotionToDto(PotionTemplate template, Integer price) {
        return PotionTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .powerBonus(template.getPowerBonus())
                .isPermanent(template.getIsPermanent())
                .calculatedPrice(price)
                .description(template.getDescription())
                .iconUrl(template.getIconUrl())
                .build();
    }

    private ClothingTemplateDto mapClothingToDto(ClothingTemplate template, Integer price) {
        return ClothingTemplateDto.builder()
                .id(template.getId())
                .name(template.getName())
                .type(template.getClothingType().name())
                .bonus(template.getBonusPercentage())
                .calculatedPrice(price)
                .description(template.getDescription())
                .iconUrl(template.getIconUrl())
                .build();
    }
}
