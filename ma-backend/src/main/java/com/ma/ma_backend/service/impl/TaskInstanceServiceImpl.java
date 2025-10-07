package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.TaskDifficulty;
import com.ma.ma_backend.domain.TaskImportance;
import com.ma.ma_backend.domain.TaskInstance;
import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.domain.UserGameStats;
import com.ma.ma_backend.dto.TaskInstanceDto;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.TaskInstanceRepository;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.service.intr.BossBattleService;
import com.ma.ma_backend.service.intr.GuildBossBattleService;
import com.ma.ma_backend.service.intr.TaskInstanceService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskInstanceServiceImpl implements TaskInstanceService {
    private final TaskInstanceRepository taskInstanceRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EntityMapper entityMapper;
    private final BossBattleService bossBattleService;
    private final @Lazy GuildBossBattleService guildBossBattleService;
    private final com.ma.ma_backend.repository.BossRepository bossRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskInstanceDto> getTaskInstancesByTaskId(Long taskId) {
        User user = userService.getLogedInUser();
        List<TaskInstance> instances = taskInstanceRepository.findByTaskIdAndTaskUserIdOrderByStartDateAsc(taskId, user.getId());
        return instances.stream()
                .map(entityMapper::taskInstanceToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskInstanceDto completeTaskInstance(Long instanceId) {
        User user = userService.getLogedInUser();
        TaskInstance instance = taskInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Task instance not found with id: " + instanceId));

        // Verify the instance belongs to the user
        if (!instance.getTask().getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Task instance not found with id: " + instanceId);
        }

        // Check if task is scheduled in the future (startDate not yet reached)
        if (instance.getStartDate().isAfter(java.time.LocalDateTime.now())) {
            throw new com.ma.ma_backend.exception.BadRequestException("Cannot complete task scheduled in the future");
        }

        // Check if task endDate has not yet passed (for non-recurring tasks with endDate)
        if (instance.getTask().getEndDate() != null &&
            instance.getTask().getEndDate().isAfter(java.time.LocalDateTime.now())) {
            throw new com.ma.ma_backend.exception.BadRequestException("Cannot complete task before end date");
        }

        // Check XP quota
        boolean canAwardXp = checkXpQuota(instance.getTask(), user);

        // Calculate XP based on task difficulty and importance (scaled by user level)
        UserGameStats gameStats = user.getGameStats();
        int baseXp = canAwardXp && gameStats != null ? calculateXP(instance.getTask(), gameStats.getLevel()) : 0;

        // Mark instance as completed
        instance.setStatus(com.ma.ma_backend.domain.TaskStatus.COMPLETED);
        instance.setCompletedAt(java.time.LocalDateTime.now());
        instance.setXpAwarded(canAwardXp);
        instance.setXpAmount(baseXp);

        TaskInstance savedInstance = taskInstanceRepository.save(instance);

        // Award XP to user's game stats only if quota allows
        if (canAwardXp && baseXp > 0 && gameStats != null) {
            int currentLevel = gameStats.getLevel();
            int maxXpForCurrentLevel = calculateRequiredXpForLevel(currentLevel);
            int currentXp = gameStats.getExperiencePoints();

            System.out.println("===== XP AWARD DEBUG =====");
            System.out.println("Current Level: " + currentLevel);
            System.out.println("Current XP: " + currentXp);
            System.out.println("Base XP from task: " + baseXp);
            System.out.println("Max XP for level " + currentLevel + ": " + maxXpForCurrentLevel);

            // Add XP but cap at max for current level
            int newXp = currentXp + baseXp;
            System.out.println("Calculated new XP (before cap): " + newXp);

            if (newXp > maxXpForCurrentLevel) {
                System.out.println("XP exceeds max! Capping to: " + maxXpForCurrentLevel);
                newXp = maxXpForCurrentLevel;

                // Create boss for current level if it doesn't exist
                if (bossRepository.findByLevel(currentLevel).isEmpty()) {
                    System.out.println("Creating boss for level " + currentLevel);
                    bossBattleService.onLevelComplete(user.getId(), currentLevel);
                } else {
                    System.out.println("Boss already exists for level " + currentLevel + ", skipping creation");
                }
            }

            System.out.println("Final XP to set: " + newXp);
            gameStats.setExperiencePoints(newXp);

            System.out.println("Saving user with level: " + gameStats.getLevel() + ", XP: " + gameStats.getExperiencePoints());
            System.out.println("========================");
            userRepository.save(user);
        }

        // Trigger guild boss battle progress
        guildBossBattleService.onTaskCompleted(
            user.getId(),
            instance.getTask().getDifficulty().name(),
            instance.getTask().getImportance().name()
        );

        return entityMapper.taskInstanceToDto(savedInstance);
    }

    @Override
    @Transactional
    public void deleteTaskInstance(Long instanceId) {
        User user = userService.getLogedInUser();
        TaskInstance instance = taskInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new NotFoundException("Task instance not found with id: " + instanceId));

        // Verify the instance belongs to the user
        if (!instance.getTask().getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Task instance not found with id: " + instanceId);
        }

        taskInstanceRepository.delete(instance);
    }

    private int calculateXP(com.ma.ma_backend.domain.Task task, int userLevel) {
        int difficultyXp = task.getDifficulty().getXp();

        // Scale importance XP based on user level
        // Formula: XP bitnosti za prethodni nivo + XP bitnosti za prethodni nivo / 2
        int baseImportanceXp = task.getImportance().getXp();
        int scaledImportanceXp = calculateScaledImportanceXp(baseImportanceXp, userLevel);

        return difficultyXp + scaledImportanceXp;
    }

    private int calculateScaledImportanceXp(int baseXp, int level) {
        if (level == 1) {
            return baseXp;
        }
        int previousLevelXp = calculateScaledImportanceXp(baseXp, level - 1);
        return previousLevelXp + previousLevelXp / 2;
    }

    private boolean checkXpQuota(com.ma.ma_backend.domain.Task task, User user) {
        LocalDateTime now = LocalDateTime.now();
        TaskDifficulty difficulty = task.getDifficulty();
        TaskImportance importance = task.getImportance();

        // Special importance: max 1 per month
        if (importance == TaskImportance.SPECIAL) {
            LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            long count = taskInstanceRepository.countCompletedByImportanceInPeriod(
                    user.getId(), TaskImportance.SPECIAL, monthStart, now
            );
            return count < 1;
        }

        // Extremely hard difficulty: max 1 per week
        if (difficulty == TaskDifficulty.EXTREMELY_HARD) {
            LocalDateTime weekStart = now.minusDays(7);
            long count = taskInstanceRepository.countCompletedByDifficultyInPeriod(
                    user.getId(), TaskDifficulty.EXTREMELY_HARD, weekStart, now
            );
            return count < 1;
        }

        // Daily quotas for difficulty + importance combinations
        LocalDateTime dayStart = now.withHour(0).withMinute(0).withSecond(0);

        // Hard + Extremely Important: max 2 per day
        if (difficulty == TaskDifficulty.HARD && importance == TaskImportance.EXTREMELY_IMPORTANT) {
            long count = taskInstanceRepository.countCompletedByDifficultyAndImportanceInPeriod(
                    user.getId(), TaskDifficulty.HARD, TaskImportance.EXTREMELY_IMPORTANT, dayStart, now
            );
            return count < 2;
        }

        // Easy + Important: max 5 per day
        if (difficulty == TaskDifficulty.EASY && importance == TaskImportance.IMPORTANT) {
            long count = taskInstanceRepository.countCompletedByDifficultyAndImportanceInPeriod(
                    user.getId(), TaskDifficulty.EASY, TaskImportance.IMPORTANT, dayStart, now
            );
            return count < 5;
        }

        // Very Easy + Normal: max 5 per day
        if (difficulty == TaskDifficulty.VERY_EASY && importance == TaskImportance.NORMAL) {
            long count = taskInstanceRepository.countCompletedByDifficultyAndImportanceInPeriod(
                    user.getId(), TaskDifficulty.VERY_EASY, TaskImportance.NORMAL, dayStart, now
            );
            return count < 5;
        }

        // For other combinations, allow XP (no quota restriction)
        return true;
    }

    private int calculateRequiredXpForLevel(int level) {
        if (level == 1) return 200;
        int previousXp = calculateRequiredXpForLevel(level - 1);
        int nextXp = previousXp * 2 + previousXp / 2;
        // Round to next hundred
        return (int) Math.ceil(nextXp / 100.0) * 100;
    }
}
