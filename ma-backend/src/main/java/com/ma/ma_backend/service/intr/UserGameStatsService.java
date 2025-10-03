package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.BadgeDto;
import com.ma.ma_backend.dto.UserGameStatsDto;

import java.util.List;

public interface UserGameStatsService {
    UserGameStatsDto getStatsByUserId(Long userId);
    UserGameStatsDto updateStats(Long userId, UserGameStatsDto statsDto);

    List<BadgeDto> getBadgesByUserId(Long userId);
    BadgeDto awardBadge(Long userId, Long badgeId);
    void removeBadge(Long userId, Long badgeId);
}
