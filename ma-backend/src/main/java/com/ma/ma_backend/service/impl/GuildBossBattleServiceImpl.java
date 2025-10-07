package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.GuildBossBattleDto;
import com.ma.ma_backend.dto.GuildBossMissionProgressDto;
import com.ma.ma_backend.dto.GuildBossMissionSummaryDto;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.repository.*;
import com.ma.ma_backend.service.intr.GuildBossBattleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildBossBattleServiceImpl implements GuildBossBattleService {

    private final GuildRepository guildRepository;
    private final GuildBossBattleRepository guildBossBattleRepository;
    private final GuildBossMissionProgressRepository progressRepository;
    private final GuildMessageDateRepository messageDateRepository;
    private final UserRepository userRepository;
    private final UserPotionRepository userPotionRepository;
    private final UserGameStatsRepository userGameStatsRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final BossRepository bossRepository;
    private final PotionTemplateRepository potionTemplateRepository;
    private final ClothingTemplateRepository clothingTemplateRepository;
    private final UserClothingRepository userClothingRepository;

    private static final int MISSION_DURATION_WEEKS = 2;
    private static final int HP_PER_MEMBER = 100;

    @Override
    @Transactional
    public GuildBossBattleDto startGuildBossBattle(Long userId, Long guildId) {
        Guild guild = guildRepository.findById(guildId)
            .orElseThrow(() -> new NotFoundException("Guild not found"));

        // Provera da li je korisnik vođa saveza
        if (!guild.getLeader().getId().equals(userId)) {
            throw new IllegalStateException("Only guild leader can start special mission");
        }

        // Provera da li već postoji aktivna misija
        if (guildBossBattleRepository.hasActiveGuildBattle(guildId)) {
            throw new IllegalStateException("Guild already has an active special mission");
        }

        // Kreiranje nove specijalne misije
        int memberCount = guild.getMembers().size();
        int maxHp = HP_PER_MEMBER * memberCount;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endsAt = now.plusWeeks(MISSION_DURATION_WEEKS);

        GuildBossBattle battle = GuildBossBattle.builder()
            .guild(guild)
            .bossName("Guild Boss Level " + memberCount)
            .maxHp(maxHp)
            .currentHp(maxHp)
            .memberCount(memberCount)
            .status(BattleStatus.IN_PROGRESS)
            .startedAt(now)
            .endsAt(endsAt)
            .build();

        battle = guildBossBattleRepository.save(battle);

        // Kreiranje napretka za svakog člana
        for (User member : guild.getMembers()) {
            GuildBossMissionProgress progress = GuildBossMissionProgress.builder()
                .guildBossBattle(battle)
                .user(member)
                .shopPurchases(0)
                .bossHits(0)
                .easyTasksCompleted(0)
                .hardTasksCompleted(0)
                .noUncompletedTasks(true)
                .daysWithMessages(0)
                .totalDamageDealt(0)
                .totalTasksCompleted(0)
                .build();
            progressRepository.save(progress);
        }

        // Ažuriranje Guild flaga
        guild.setHasActiveMission(true);
        guildRepository.save(guild);

        return mapToDto(battle);
    }

    @Override
    @Transactional(readOnly = true)
    public GuildBossBattleDto getActiveGuildBossBattle(Long guildId) {
        log.info("SERVICE: getActiveGuildBossBattle called with guildId: {}", guildId);
        try {
            GuildBossBattle battle = guildBossBattleRepository
                .findActiveGuildBattle(guildId)
                .orElse(null);
            log.info("SERVICE: Battle found: {}", battle != null ? battle.getId() : "null");

            GuildBossBattleDto dto = battle != null ? mapToDto(battle) : null;
            log.info("SERVICE: DTO mapped: {}", dto != null ? dto.getId() : "null");
            return dto;
        } catch (Exception e) {
            log.error("SERVICE: Error in getActiveGuildBossBattle", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GuildBossMissionSummaryDto getGuildBossBattleProgress(Long guildId) {
        GuildBossBattle battle = guildBossBattleRepository
            .findActiveGuildBattle(guildId)
            .orElseThrow(() -> new NotFoundException("No active guild boss battle found"));

        List<GuildBossMissionProgress> progressList = progressRepository
            .findByBattleIdOrderByDamageDesc(battle.getId());

        List<GuildBossMissionProgressDto> progressDtos = progressList.stream()
            .map(this::mapToProgressDto)
            .collect(Collectors.toList());

        int totalDamage = progressList.stream()
            .mapToInt(GuildBossMissionProgress::getTotalDamageDealt)
            .sum();

        return GuildBossMissionSummaryDto.builder()
            .battle(mapToDto(battle))
            .memberProgress(progressDtos)
            .totalDamageDealt(totalDamage)
            .isActive(true)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GuildBossMissionProgressDto getUserProgress(Long userId, Long guildId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getCurrentGuild() == null || !user.getCurrentGuild().getId().equals(guildId)) {
            throw new IllegalStateException("User is not a member of this guild");
        }

        GuildBossMissionProgress progress = progressRepository
            .findActiveProgressForUser(guildId, userId)
            .orElseThrow(() -> new NotFoundException("No active mission progress found for user"));

        return mapToProgressDto(progress);
    }

    @Override
    @Transactional
    public void onShopPurchase(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getCurrentGuild() == null) {
            return; // Korisnik nije u savezu
        }

        GuildBossMissionProgress progress = progressRepository
            .findActiveProgressForUser(user.getCurrentGuild().getId(), userId)
            .orElse(null);

        if (progress == null) {
            return; // Nema aktivne misije
        }

        if (progress.canAddShopPurchase()) {
            progress.setShopPurchases(progress.getShopPurchases() + 1);
            int damage = 2;
            applyDamage(progress, damage);
            progressRepository.save(progress);
        }
    }

    @Override
    @Transactional
    public void onBossHit(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getCurrentGuild() == null) {
            return;
        }

        GuildBossMissionProgress progress = progressRepository
            .findActiveProgressForUser(user.getCurrentGuild().getId(), userId)
            .orElse(null);

        if (progress == null) {
            return;
        }

        if (progress.canAddBossHit()) {
            progress.setBossHits(progress.getBossHits() + 1);
            int damage = 2;
            applyDamage(progress, damage);
            progressRepository.save(progress);
        }
    }

    @Override
    @Transactional
    public void onTaskCompleted(Long userId, String difficulty, String importance) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getCurrentGuild() == null) {
            return;
        }

        GuildBossMissionProgress progress = progressRepository
            .findActiveProgressForUser(user.getCurrentGuild().getId(), userId)
            .orElse(null);

        if (progress == null) {
            return;
        }

        TaskDifficulty taskDifficulty;
        TaskImportance taskImportance;

        try {
            taskDifficulty = TaskDifficulty.valueOf(difficulty);
            taskImportance = TaskImportance.valueOf(importance);
        } catch (IllegalArgumentException e) {
            return; // Invalid difficulty/importance
        }

        // Kategorisanje zadatka
        boolean isEasyTask = isEasyTask(taskDifficulty, taskImportance);

        if (isEasyTask) {
            if (progress.canAddEasyTask()) {
                progress.setEasyTasksCompleted(progress.getEasyTasksCompleted() + 1);
                int damage = isNormalOrEasyTask(taskDifficulty, taskImportance) ? 2 : 1;
                applyDamage(progress, damage);
                progress.setTotalTasksCompleted(progress.getTotalTasksCompleted() + 1);
            }
        } else {
            if (progress.canAddHardTask()) {
                progress.setHardTasksCompleted(progress.getHardTasksCompleted() + 1);
                int damage = 4;
                applyDamage(progress, damage);
                progress.setTotalTasksCompleted(progress.getTotalTasksCompleted() + 1);
            }
        }

        progressRepository.save(progress);
    }

    @Override
    @Transactional
    public void onGuildMessage(Long userId, Long guildId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        GuildBossMissionProgress progress = progressRepository
            .findActiveProgressForUser(guildId, userId)
            .orElse(null);

        if (progress == null) {
            return; // Nema aktivne misije
        }

        LocalDate today = LocalDate.now();

        // Provera da li je već poslao poruku danas
        if (messageDateRepository.findByProgressIdAndDate(progress.getId(), today).isEmpty()) {
            // Prvi put šalje poruku danas
            GuildMessageDate messageDate = GuildMessageDate.builder()
                .missionProgress(progress)
                .messageDate(today)
                .build();
            messageDateRepository.save(messageDate);

            // Ažuriranje broja dana
            long daysCount = messageDateRepository.countDaysWithMessages(progress.getId());
            progress.setDaysWithMessages((int) daysCount);

            // Dodavanje damage-a za ovaj dan
            int damage = 4;
            applyDamage(progress, damage);
            progressRepository.save(progress);
        }
    }

    @Override
    @Transactional
    public void checkAndCompleteExpiredMissions() {
        List<GuildBossBattle> activeBattles = guildBossBattleRepository
            .findAll()
            .stream()
            .filter(b -> b.getStatus() == BattleStatus.IN_PROGRESS)
            .filter(b -> b.getEndsAt().isBefore(LocalDateTime.now()))
            .collect(Collectors.toList());

        for (GuildBossBattle battle : activeBattles) {
            completeMission(battle);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuildBossBattleDto> getGuildBossBattleHistory(Long guildId) {
        return guildBossBattleRepository
            .findByGuildIdOrderByStartedAtDesc(guildId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    // Helper methods

    private void applyDamage(GuildBossMissionProgress progress, int damage) {
        progress.addDamage(damage);

        GuildBossBattle battle = progress.getGuildBossBattle();
        int newHp = Math.max(0, battle.getCurrentHp() - damage);
        battle.setCurrentHp(newHp);

        if (newHp <= 0) {
            completeMission(battle);
        } else {
            guildBossBattleRepository.save(battle);
        }
    }

    private void completeMission(GuildBossBattle battle) {
        boolean victory = battle.getCurrentHp() <= 0;

        battle.setStatus(victory ? BattleStatus.WON : BattleStatus.LOST);
        battle.setCompletedAt(LocalDateTime.now());
        guildBossBattleRepository.save(battle);

        // Ažuriranje Guild flaga
        Guild guild = battle.getGuild();
        guild.setHasActiveMission(false);
        guildRepository.save(guild);

        if (victory) {
            // Dodela nagrada
            List<GuildBossMissionProgress> progressList = progressRepository
                .findByGuildBossBattleId(battle.getId());

            for (GuildBossMissionProgress progress : progressList) {
                giveRewards(progress);

                // Provera i dodela bonusa za "bez nerešenih zadataka"
                if (hasNoUncompletedTasks(progress.getUser().getId(), battle.getStartedAt(), battle.getCompletedAt())) {
                    progress.setNoUncompletedTasks(true);
                    int damage = 10;
                    progress.addDamage(damage);
                } else {
                    progress.setNoUncompletedTasks(false);
                }

                progressRepository.save(progress);
            }
        }
    }

    private void giveRewards(GuildBossMissionProgress progress) {
        User user = progress.getUser();
        UserGameStats userStats = user.getGameStats();

        // 1. Jedan napitak
        List<PotionTemplate> potions = potionTemplateRepository.findAll();
        if (!potions.isEmpty()) {
            PotionTemplate randomPotion = potions.get((int) (Math.random() * potions.size()));
            UserPotion userPotion = UserPotion.builder()
                .userGameStats(userStats)
                .potionTemplate(randomPotion)
                .build();
            userPotionRepository.save(userPotion);
        }

        // 2. Jedan komad odeće
        List<ClothingTemplate> clothingList = clothingTemplateRepository.findAll();
        if (!clothingList.isEmpty()) {
            ClothingTemplate randomClothing = clothingList.get((int) (Math.random() * clothingList.size()));
            UserClothing userClothing = UserClothing.builder()
                .userGameStats(userStats)
                .clothingTemplate(randomClothing)
                .accumulatedBonus(0)
                .battlesRemaining(2)
                .isActive(false)
                .build();
            userClothingRepository.save(userClothing);
        }

        // 3. 50% novčića od nagrade narednog bosa
        int nextLevel = userStats.getLevel() + 1;
        Boss nextBoss = bossRepository.findByLevel(nextLevel).orElse(null);
        if (nextBoss != null) {
            int coinReward = nextBoss.getCoinReward() / 2;
            userStats.setCoins(userStats.getCoins() + coinReward);
            userGameStatsRepository.save(userStats);
        }

        // Badge se automatski izračunava na osnovu totalTasksCompleted
    }

    private boolean hasNoUncompletedTasks(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<TaskInstance> tasks = taskInstanceRepository
            .findByTaskUserId(userId);

        return tasks.stream()
            .filter(t -> t.getStartDate().isAfter(startDate) && t.getStartDate().isBefore(endDate))
            .noneMatch(t -> t.getStatus() == TaskStatus.ACTIVE);
    }

    private boolean isEasyTask(TaskDifficulty difficulty, TaskImportance importance) {
        return difficulty == TaskDifficulty.VERY_EASY ||
               difficulty == TaskDifficulty.EASY ||
               (difficulty == TaskDifficulty.HARD && importance == TaskImportance.NORMAL) ||
               (difficulty == TaskDifficulty.HARD && importance == TaskImportance.IMPORTANT);
    }

    private boolean isNormalOrEasyTask(TaskDifficulty difficulty, TaskImportance importance) {
        return difficulty == TaskDifficulty.EASY && importance == TaskImportance.NORMAL;
    }

    private GuildBossBattleDto mapToDto(GuildBossBattle battle) {
        double progressPercentage = 100.0 - ((double) battle.getCurrentHp() / battle.getMaxHp() * 100.0);

        return GuildBossBattleDto.builder()
            .id(battle.getId())
            .guildId(battle.getGuild().getId())
            .guildName(battle.getGuild().getName())
            .bossName(battle.getBossName())
            .maxHp(battle.getMaxHp())
            .currentHp(battle.getCurrentHp())
            .memberCount(battle.getMemberCount())
            .status(battle.getStatus().name())
            .startedAt(battle.getStartedAt())
            .endsAt(battle.getEndsAt())
            .completedAt(battle.getCompletedAt())
            .progressPercentage(progressPercentage)
            .build();
    }

    private GuildBossMissionProgressDto mapToProgressDto(GuildBossMissionProgress progress) {
        GuildBossBadge badge = GuildBossBadge.fromTasksCompleted(progress.getTotalTasksCompleted());

        return GuildBossMissionProgressDto.builder()
            .id(progress.getId())
            .userId(progress.getUser().getId())
            .username(progress.getUser().getUsername())
            .shopPurchases(progress.getShopPurchases())
            .bossHits(progress.getBossHits())
            .easyTasksCompleted(progress.getEasyTasksCompleted())
            .hardTasksCompleted(progress.getHardTasksCompleted())
            .noUncompletedTasks(progress.getNoUncompletedTasks())
            .daysWithMessages(progress.getDaysWithMessages())
            .totalDamageDealt(progress.getTotalDamageDealt())
            .totalTasksCompleted(progress.getTotalTasksCompleted())
            .badge(badge.name())
            .badgeTitle(badge.getTitle())
            .build();
    }
}
