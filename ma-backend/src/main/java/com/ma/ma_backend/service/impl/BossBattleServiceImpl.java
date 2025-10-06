package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.*;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.repository.*;
import com.ma.ma_backend.service.intr.BossBattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BossBattleServiceImpl implements BossBattleService {

    private final BossRepository bossRepository;
    private final BossBattleRepository bossBattleRepository;
    private final UserBossProgressRepository userBossProgressRepository;
    private final UserGameStatsRepository userGameStatsRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final WeaponTemplateRepository weaponTemplateRepository;
    private final ClothingTemplateRepository clothingTemplateRepository;
    private final UserWeaponRepository userWeaponRepository;
    private final UserClothingRepository userClothingRepository;
    private final Random random = new Random();

    private static final int MAX_ATTACKS = 5;
    private static final double EQUIPMENT_DROP_CHANCE = 1.0; // 100% (for testing)
    private static final double WEAPON_DROP_CHANCE = 0.05; // 5% of equipment drops
    private static final double CLOTHING_DROP_CHANCE = 0.95; // 95% of equipment drops
    private static final double HALF_REWARD_HP_THRESHOLD = 0.50; // 50% HP remaining

    @Override
    @Transactional(readOnly = true)
    public BossDto getNextBoss(Long userId) {
        UserGameStats userGameStats = getUserGameStats(userId);

        // Check if user has max XP for current level
        int currentLevel = userGameStats.getLevel();
        int currentXp = userGameStats.getExperiencePoints();
        int maxXpForLevel = calculateRequiredXpForLevel(currentLevel);

        if (currentXp < maxXpForLevel) {
            throw new IllegalStateException("Boss not available. Need " + maxXpForLevel + " XP to challenge boss.");
        }

        // Check for pending bosses first
        List<UserBossProgress> pendingBosses = userBossProgressRepository
            .findPendingBossesSorted(userGameStats.getId());

        if (!pendingBosses.isEmpty()) {
            Boss boss = pendingBosses.get(0).getBoss();
            return mapToBossDto(boss);
        }

        // Get the boss for the current level
        Boss boss = bossRepository.findByLevel(userGameStats.getLevel())
            .orElseThrow(() -> new NotFoundException(
                "Boss not found for level " + userGameStats.getLevel()));

        return mapToBossDto(boss);
    }

    @Override
    @Transactional
    public BossBattleDto startBattle(Long userId) {
        UserGameStats userGameStats = getUserGameStats(userId);

        // Check if there's already an active battle
        List<BossBattle> activeBattles = bossBattleRepository
            .findByUserGameStatsIdAndStatus(userGameStats.getId(), BattleStatus.IN_PROGRESS);

        if (!activeBattles.isEmpty()) {
            return mapToBossBattleDto(activeBattles.get(0));
        }

        // Get the next boss
        Boss nextBoss = getNextBossEntity(userGameStats);

        // Calculate user's current PP and success rate
        Integer userPP = calculateTotalPP(userGameStats);
        Double successRate = calculateSuccessRate(userId, userGameStats.getLevel());

        // Create new battle
        BossBattle battle = BossBattle.builder()
            .userGameStats(userGameStats)
            .boss(nextBoss)
            .currentHp(nextBoss.getMaxHp())
            .attacksUsed(0)
            .status(BattleStatus.IN_PROGRESS)
            .userPpAtBattle(userPP)
            .successRateAtBattle(successRate)
            .build();

        battle = bossBattleRepository.save(battle);

        return mapToBossBattleDto(battle);
    }

    @Override
    @Transactional
    public AttackResponse attack(Long userId, Long battleId, List<Long> activeWeaponIds, List<Long> activeClothingIds) {
        UserGameStats userGameStats = getUserGameStats(userId);

        BossBattle battle = bossBattleRepository
            .findByIdAndUserGameStatsId(battleId, userGameStats.getId())
            .orElseThrow(() -> new NotFoundException("Battle not found"));

        if (battle.getStatus() != BattleStatus.IN_PROGRESS) {
            throw new IllegalStateException("Battle is not in progress");
        }

        if (battle.getAttacksUsed() >= MAX_ATTACKS) {
            throw new IllegalStateException("No attacks remaining");
        }

        // Calculate if attack hits based on success rate
        boolean hit = random.nextDouble() < battle.getSuccessRateAtBattle();

        int damageDealt = 0;
        if (hit) {
            damageDealt = battle.getUserPpAtBattle();
            int newHp = Math.max(0, battle.getCurrentHp() - damageDealt);
            battle.setCurrentHp(newHp);
        }

        battle.setAttacksUsed(battle.getAttacksUsed() + 1);

        // Check if battle is complete
        boolean battleComplete = false;
        String battleResult = null;
        BattleRewardsDto rewards = null;

        if (battle.getCurrentHp() <= 0) {
            // Boss defeated!
            battleComplete = true;
            battleResult = "WON";
            battle.setStatus(BattleStatus.WON);
            battle.setCompletedAt(LocalDateTime.now());
            rewards = calculateRewards(battle, true);
            applyRewards(userGameStats, battle, rewards);
            markBossAsDefeated(userGameStats, battle.getBoss());

            // Level up only if boss level matches current user level (ensures one-time level up)
            if (battle.getBoss().getLevel().equals(userGameStats.getLevel())) {
                performLevelUp(userGameStats);
            }

        } else if (battle.getAttacksUsed() >= MAX_ATTACKS) {
            // Out of attacks
            battleComplete = true;
            double hpPercentageRemaining = (double) battle.getCurrentHp() / battle.getBoss().getMaxHp();

            if (hpPercentageRemaining <= HALF_REWARD_HP_THRESHOLD) {
                // Got boss below 50% HP - partial rewards
                battleResult = "LOST";
                battle.setStatus(BattleStatus.LOST);
                battle.setCompletedAt(LocalDateTime.now());
                rewards = calculateRewards(battle, false);
                applyRewards(userGameStats, battle, rewards);

                // Level up even on loss if boss was brought below 50% HP
                if (battle.getBoss().getLevel().equals(userGameStats.getLevel())) {
                    performLevelUp(userGameStats);
                }
            } else {
                // Failed to get boss below 50% HP - no rewards
                battleResult = "LOST";
                battle.setStatus(BattleStatus.LOST);
                battle.setCompletedAt(LocalDateTime.now());
                rewards = new BattleRewardsDto(0, null, null, null);
            }
        }

        bossBattleRepository.save(battle);

        AttackResponse response = new AttackResponse();
        response.setHit(hit);
        response.setDamageDealt(damageDealt);
        response.setBossCurrentHp(battle.getCurrentHp());
        response.setAttacksRemaining(MAX_ATTACKS - battle.getAttacksUsed());
        response.setBattleComplete(battleComplete);
        response.setBattleResult(battleResult);
        response.setRewards(rewards);

        // Decrease clothing battles remaining
        if (activeClothingIds != null && !activeClothingIds.isEmpty()) {
            decreaseClothingBattles(activeClothingIds);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BossBattleDto getCurrentBattle(Long userId) {
        UserGameStats userGameStats = getUserGameStats(userId);

        List<BossBattle> activeBattles = bossBattleRepository
            .findByUserGameStatsIdAndStatus(userGameStats.getId(), BattleStatus.IN_PROGRESS);

        if (activeBattles.isEmpty()) {
            return null;
        }

        return mapToBossBattleDto(activeBattles.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BossBattleDto> getBattleHistory(Long userId) {
        UserGameStats userGameStats = getUserGameStats(userId);

        // Get all completed battles (won or lost)
        List<BossBattle> wonBattles = bossBattleRepository
            .findByUserGameStatsIdAndStatus(userGameStats.getId(), BattleStatus.WON);
        List<BossBattle> lostBattles = bossBattleRepository
            .findByUserGameStatsIdAndStatus(userGameStats.getId(), BattleStatus.LOST);

        List<BossBattle> allBattles = new java.util.ArrayList<>(wonBattles);
        allBattles.addAll(lostBattles);

        return allBattles.stream()
            .sorted((b1, b2) -> b2.getCompletedAt().compareTo(b1.getCompletedAt()))
            .map(this::mapToBossBattleDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void onLevelComplete(Long userId, Integer newLevel) {
        System.out.println("===== onLevelComplete DEBUG =====");
        System.out.println("User ID: " + userId);
        System.out.println("New Level: " + newLevel);

        UserGameStats userGameStats = getUserGameStats(userId);
        System.out.println("UserGameStats ID: " + userGameStats.getId());

        // Check if there's a boss for this level
        Boss boss = bossRepository.findByLevel(newLevel).orElse(null);
        System.out.println("Boss found for level " + newLevel + ": " + (boss != null));

        if (boss == null) {
            System.out.println("Boss not found for level " + newLevel + ", creating new boss");
            boss = createBossForLevel(newLevel);
            System.out.println("Boss created with ID: " + boss.getId() + ", Name: " + boss.getName());
        } else {
            return;
        }
    }

    // Helper methods

    private Boss createBossForLevel(Integer level) {
        int hp, coinReward;

        if (level == 1) {
            hp = 200;
            coinReward = 200;
        } else {
            // Calculate HP and coin rewards based on previous level
            Boss previousBoss = bossRepository.findByLevel(level - 1).orElse(null);

            if (previousBoss != null) {
                // HP formula: HP_previous * 2 + HP_previous / 2
                hp = previousBoss.getMaxHp() * 2 + previousBoss.getMaxHp() / 2;
                // Coin reward formula: +20% from previous
                coinReward = (int) (previousBoss.getCoinReward() * 1.2);
            } else {
                // Recursively calculate if previous boss doesn't exist
                Boss createdPrevious = createBossForLevel(level - 1);
                hp = createdPrevious.getMaxHp() * 2 + createdPrevious.getMaxHp() / 2;
                coinReward = (int) (createdPrevious.getCoinReward() * 1.2);
            }
        }

        Boss boss = Boss.builder()
            .level(level)
            .maxHp(hp)
            .coinReward(coinReward)
            .name("Boss Level " + level)
            .description("A fearsome boss that appears at level " + level)
            .build();

        return bossRepository.save(boss);
    }

    private UserGameStats getUserGameStats(Long userId) {
        return userGameStatsRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("User game stats not found"));
    }

    private Boss getNextBossEntity(UserGameStats userGameStats) {
        // Check for pending bosses first
        List<UserBossProgress> pendingBosses = userBossProgressRepository
            .findPendingBossesSorted(userGameStats.getId());

        if (!pendingBosses.isEmpty()) {
            return pendingBosses.get(0).getBoss();
        }

        // Get the boss for the current level
        return bossRepository.findByLevel(userGameStats.getLevel())
            .orElseThrow(() -> new NotFoundException(
                "Boss not found for level " + userGameStats.getLevel()));
    }

    private Integer calculateTotalPP(UserGameStats userGameStats) {
        Integer basePP = userGameStats.getPowerPoints();

        // Add PP from active weapons
        List<UserWeapon> weapons = userWeaponRepository.findByUserGameStatsId(userGameStats.getId());
        double weaponBonus = weapons.stream()
            .filter(w -> w.getCurrentBonusPercentage() != null)
            .mapToDouble(UserWeapon::getCurrentBonusPercentage)
            .sum();

        // Add PP from active clothing
        List<UserClothing> clothing = userClothingRepository.findByUserGameStatsId(userGameStats.getId());
        int clothingBonus = clothing.stream()
            .filter(c -> c.getIsActive() != null && c.getIsActive())
            .filter(c -> c.getAccumulatedBonus() != null)
            .mapToInt(UserClothing::getAccumulatedBonus)
            .sum();

        return basePP + (int) (basePP * weaponBonus / 100.0) + clothingBonus;
    }

    private Double calculateSuccessRate(Long userId, Integer currentLevel) {
        // Calculate success rate based on task completion in current "etapa"
        // Etapa = period between last level up and now
        // For now, we'll use a simple calculation based on all tasks

        List<TaskInstance> completedTasks = taskInstanceRepository
            .findByTaskUserIdAndStatus(userId, TaskStatus.COMPLETED);

        List<TaskInstance> allTasks = taskInstanceRepository
            .findByTaskUserId(userId);

        // Exclude CANCELLED tasks
        long totalTasks = allTasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.CANCELLED)
            .count();

        if (totalTasks == 0) {
            return 0.5; // Default 50% success rate if no tasks
        }

        return (double) completedTasks.size() / totalTasks;
    }

    private BattleRewardsDto calculateRewards(BossBattle battle, boolean fullReward) {
        int coins = battle.getBoss().getCoinReward();

        if (!fullReward) {
            coins = coins / 2;
        }

        battle.setCoinsEarned(coins);

        // Equipment drop chance
        boolean dropEquipment = random.nextDouble() < EQUIPMENT_DROP_CHANCE;
        if (!fullReward) {
            dropEquipment = random.nextDouble() < (EQUIPMENT_DROP_CHANCE / 2);
        }

        if (dropEquipment) {
            boolean isWeapon = random.nextDouble() < WEAPON_DROP_CHANCE;

            if (isWeapon) {
                // Drop a random weapon
                List<WeaponTemplate> allWeapons = weaponTemplateRepository.findAll();
                if (!allWeapons.isEmpty()) {
                    WeaponTemplate weapon = allWeapons.get(random.nextInt(allWeapons.size()));
                    battle.setEquipmentEarnedWeapon(weapon);
                    return new BattleRewardsDto(coins, "WEAPON", weapon.getName(), weapon.getId());
                }
            } else {
                // Drop random clothing
                List<ClothingTemplate> allClothing = clothingTemplateRepository.findAll();
                if (!allClothing.isEmpty()) {
                    ClothingTemplate clothing = allClothing.get(random.nextInt(allClothing.size()));
                    battle.setEquipmentEarnedClothing(clothing);
                    return new BattleRewardsDto(coins, "CLOTHING", clothing.getName(), clothing.getId());
                }
            }
        }

        return new BattleRewardsDto(coins, null, null, null);
    }

    private void applyRewards(UserGameStats userGameStats, BossBattle battle, BattleRewardsDto rewards) {
        // Add coins
        userGameStats.setCoins(userGameStats.getCoins() + rewards.getCoinsEarned());
        userGameStatsRepository.save(userGameStats);

        // Add equipment if dropped
        if ("WEAPON".equals(rewards.getEquipmentType()) && battle.getEquipmentEarnedWeapon() != null) {
            UserWeapon userWeapon = UserWeapon.builder()
                .userGameStats(userGameStats)
                .weaponTemplate(battle.getEquipmentEarnedWeapon())
                .currentBonusPercentage(battle.getEquipmentEarnedWeapon().getBaseBonusPercentage())
                .upgradeLevel(0)
                .duplicateCount(0)
                .build();
            userWeaponRepository.save(userWeapon);
        } else if ("CLOTHING".equals(rewards.getEquipmentType()) && battle.getEquipmentEarnedClothing() != null) {
            UserClothing userClothing = UserClothing.builder()
                .userGameStats(userGameStats)
                .clothingTemplate(battle.getEquipmentEarnedClothing())
                .accumulatedBonus(0)
                .battlesRemaining(2)
                .isActive(false)
                .build();
            userClothingRepository.save(userClothing);
        }
    }

    private void markBossAsDefeated(UserGameStats userGameStats, Boss boss) {
        UserBossProgress progress = userBossProgressRepository
            .findByUserGameStatsIdAndBossId(userGameStats.getId(), boss.getId())
            .orElse(null);

        if (progress != null) {
            progress.setDefeated(true);
            progress.setDefeatedAt(LocalDateTime.now());
            userBossProgressRepository.save(progress);
        } else {
            // Create progress entry as defeated
            progress = UserBossProgress.builder()
                .userGameStats(userGameStats)
                .boss(boss)
                .defeated(true)
                .defeatedAt(LocalDateTime.now())
                .build();
            userBossProgressRepository.save(progress);
        }
    }

    private void decreaseClothingBattles(List<Long> clothingIds) {
        for (Long clothingId : clothingIds) {
            UserClothing clothing = userClothingRepository.findById(clothingId).orElse(null);
            if (clothing != null && clothing.getIsActive()) {
                clothing.setBattlesRemaining(clothing.getBattlesRemaining() - 1);
                if (clothing.getBattlesRemaining() <= 0) {
                    clothing.setIsActive(false);
                }
                userClothingRepository.save(clothing);
            }
        }
    }

    private BossDto mapToBossDto(Boss boss) {
        BossDto dto = new BossDto();
        dto.setId(boss.getId());
        dto.setLevel(boss.getLevel());
        dto.setMaxHp(boss.getMaxHp());
        dto.setCoinReward(boss.getCoinReward());
        dto.setName(boss.getName());
        dto.setDescription(boss.getDescription());
        dto.setImageUrl(boss.getImageUrl());
        return dto;
    }

    private BossBattleDto mapToBossBattleDto(BossBattle battle) {
        BossBattleDto dto = new BossBattleDto();
        dto.setId(battle.getId());
        dto.setBoss(mapToBossDto(battle.getBoss()));
        dto.setCurrentHp(battle.getCurrentHp());
        dto.setAttacksUsed(battle.getAttacksUsed());
        dto.setStatus(battle.getStatus().name());
        dto.setCoinsEarned(battle.getCoinsEarned());
        dto.setUserPpAtBattle(battle.getUserPpAtBattle());
        dto.setSuccessRateAtBattle(battle.getSuccessRateAtBattle());
        dto.setCreatedAt(battle.getCreatedAt());
        dto.setCompletedAt(battle.getCompletedAt());

        if (battle.getEquipmentEarnedWeapon() != null) {
            dto.setEquipmentEarnedType("WEAPON");
            dto.setEquipmentEarnedName(battle.getEquipmentEarnedWeapon().getName());
        } else if (battle.getEquipmentEarnedClothing() != null) {
            dto.setEquipmentEarnedType("CLOTHING");
            dto.setEquipmentEarnedName(battle.getEquipmentEarnedClothing().getName());
        }

        return dto;
    }

    private int calculateRequiredXpForLevel(int level) {
        if (level == 1) return 200;
        int previousXp = calculateRequiredXpForLevel(level - 1);
        int nextXp = previousXp * 2 + previousXp / 2;
        // Round to next hundred
        return (int) Math.ceil(nextXp / 100.0) * 100;
    }

    private int calculatePPGainForLevel(int newLevel) {
        // Level 2 gains 40 PP
        if (newLevel == 2) return 40;

        // For levels > 2: PP_previous + (3/4 * PP_previous) = PP_previous * 1.75
        int previousPP = calculatePPGainForLevel(newLevel - 1);
        return (int) (previousPP + (previousPP * 3.0 / 4.0));
    }

    private Title getTitleForLevel(int level) {
        if (level <= 1) return Title.NOVICE;
        if (level == 2) return Title.APPRENTICE;
        if (level == 3) return Title.ADVENTURER;
        if (level == 4) return Title.WARRIOR;
        if (level == 5) return Title.CHAMPION;
        if (level == 6) return Title.MASTER;
        if (level == 7) return Title.LEGEND;
        return Title.MYTHIC; // Level 8+
    }

    private void performLevelUp(UserGameStats userGameStats) {
        int newLevel = userGameStats.getLevel() + 1;
        int ppGain = calculatePPGainForLevel(newLevel);
        Title newTitle = getTitleForLevel(newLevel);

        System.out.println("===== LEVEL UP DEBUG =====");
        System.out.println("Old Level: " + userGameStats.getLevel());
        System.out.println("New Level: " + newLevel);
        System.out.println("Old PP: " + userGameStats.getPowerPoints());
        System.out.println("PP Gain: " + ppGain);
        System.out.println("New PP: " + (userGameStats.getPowerPoints() + ppGain));
        System.out.println("Old Title: " + userGameStats.getTitle());
        System.out.println("New Title: " + newTitle);
        System.out.println("==========================");

        userGameStats.setLevel(newLevel);
        userGameStats.setPowerPoints(userGameStats.getPowerPoints() + ppGain);
        userGameStats.setTitle(newTitle);
        userGameStatsRepository.save(userGameStats);
    }

    @Override
    @Transactional(readOnly = true)
    public BattleStatsPreviewDto getBattleStatsPreview(Long userId) {
        UserGameStats userGameStats = getUserGameStats(userId);

        Integer userPP = calculateTotalPP(userGameStats);
        Double successRate = calculateSuccessRate(userId, userGameStats.getLevel());

        return new BattleStatsPreviewDto(userPP, successRate, MAX_ATTACKS);
    }
}
