package com.ma.ma_backend.mapper;

import com.ma.ma_backend.domain.Badge;
import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.domain.UserGameStats;
import com.ma.ma_backend.dto.BadgeDto;
import com.ma.ma_backend.dto.PublicUserProfileDto;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.dto.UserGameStatsDto;
import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EntityMapper {
    public UserDto mapUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().name());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setAvatarId(user.getAvatarId());

        if (user.getGameStats() != null) {
            userDto.setGameStats(userGameStatsToDto(user.getGameStats()));
        }

        if (user.getCurrentGuild() != null) {
            userDto.setGuildId(user.getCurrentGuild().getId());
            userDto.setGuildName(user.getCurrentGuild().getName());
        }

        return userDto;
    }

    public UserGameStatsDto userGameStatsToDto(UserGameStats stats) {
        UserGameStatsDto dto = new UserGameStatsDto();
        dto.setId(stats.getId());
        dto.setLevel(stats.getLevel());
        dto.setTitle(stats.getTitle() != null ? stats.getTitle().name() : null);
        dto.setPowerPoints(stats.getPowerPoints());
        dto.setExperiencePoints(stats.getExperiencePoints());
        dto.setCoins(stats.getCoins());
        dto.setQrCode(stats.getQrCode());
        return dto;
    }

    public BadgeDto badgeToDto(Badge badge) {
        BadgeDto dto = new BadgeDto();
        dto.setId(badge.getId());
        dto.setName(badge.getName());
        dto.setDescription(badge.getDescription());
        dto.setIconUrl(badge.getIconUrl());
        return dto;
    }

    public PublicUserProfileDto userToPublicProfile(User user) {
        PublicUserProfileDto dto = new PublicUserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatarId(user.getAvatarId());

        if (user.getGameStats() != null) {
            UserGameStats stats = user.getGameStats();
            dto.setLevel(stats.getLevel());
            dto.setTitle(stats.getTitle() != null ? stats.getTitle().name() : null);
            dto.setExperiencePoints(stats.getExperiencePoints());
            dto.setQrCode(stats.getQrCode());
            dto.setBadgeCount(stats.getEarnedBadges().size());
            dto.setBadges(stats.getEarnedBadges().stream()
                    .map(this::badgeToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public CategoryDto categoryToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .color(category.getColor())
                .userId(category.getUser().getId())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public TaskDto taskToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .categoryId(task.getCategory().getId())
                .categoryName(task.getCategory().getName())
                .categoryColor(task.getCategory().getColor())
                .userId(task.getUser().getId())
                .difficulty(task.getDifficulty().name())
                .importance(task.getImportance().name())
                .totalXp(task.getTotalXp())
                .isRecurring(task.getIsRepeating())
                .recurrenceInterval(task.getRecurrenceInterval())
                .recurrenceUnit(task.getRecurrenceUnit() != null ? task.getRecurrenceUnit().name() : null)
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public TaskInstanceDto taskInstanceToDto(TaskInstance taskInstance) {
        Task task = taskInstance.getTask();
        return TaskInstanceDto.builder()
                .id(taskInstance.getId())
                .taskId(task.getId())
                .taskTitle(task.getTitle())
                .taskDescription(task.getDescription())
                .categoryId(task.getCategory().getId())
                .categoryName(task.getCategory().getName())
                .categoryColor(task.getCategory().getColor())
                .difficulty(task.getDifficulty().name())
                .importance(task.getImportance().name())
                .status(taskInstance.getStatus().name())
                .startDate(taskInstance.getStartDate())
                .completedAt(taskInstance.getCompletedAt())
                .xpAwarded(taskInstance.getXpAwarded())
                .xpAmount(taskInstance.getXpAmount())
                .createdAt(taskInstance.getCreatedAt())
                .updatedAt(taskInstance.getUpdatedAt())
                .build();
    }
}
