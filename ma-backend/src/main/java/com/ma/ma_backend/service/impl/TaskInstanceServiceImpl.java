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
import com.ma.ma_backend.service.intr.TaskInstanceService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
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

        // Calculate XP based on task difficulty and importance
        int xp = canAwardXp ? calculateXP(instance.getTask()) : 0;

        // Mark instance as completed
        instance.setStatus(com.ma.ma_backend.domain.TaskStatus.COMPLETED);
        instance.setCompletedAt(java.time.LocalDateTime.now());
        instance.setXpAwarded(canAwardXp);
        instance.setXpAmount(xp);

        TaskInstance savedInstance = taskInstanceRepository.save(instance);

        // Award XP to user's game stats only if quota allows
        if (canAwardXp && xp > 0) {
            UserGameStats gameStats = user.getGameStats();
            if (gameStats != null) {
                gameStats.setExperiencePoints(gameStats.getExperiencePoints() + xp);
                userRepository.save(user);
            }
        }

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

    private int calculateXP(com.ma.ma_backend.domain.Task task) {
        int difficultyXp = task.getDifficulty().getXp();
        int importanceXp = task.getImportance().getXp();
        return difficultyXp + importanceXp;
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
}
