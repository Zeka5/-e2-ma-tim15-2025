package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.Badge;
import com.ma.ma_backend.domain.UserGameStats;
import com.ma.ma_backend.dto.BadgeDto;
import com.ma.ma_backend.dto.UserGameStatsDto;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.BadgeRepository;
import com.ma.ma_backend.repository.UserGameStatsRepository;
import com.ma.ma_backend.service.intr.UserGameStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGameStatsServiceImpl implements UserGameStatsService {
    private final UserGameStatsRepository userGameStatsRepository;
    private final BadgeRepository badgeRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional(readOnly = true)
    public UserGameStatsDto getStatsByUserId(Long userId) {
        UserGameStats stats = userGameStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User game stats not found for user id: " + userId));

        UserGameStatsDto dto = entityMapper.userGameStatsToDto(stats);
        dto.setBadgeCount(stats.getEarnedBadges().size());
        return dto;
    }

    @Override
    @Transactional
    public UserGameStatsDto updateStats(Long userId, UserGameStatsDto statsDto) {
        UserGameStats stats = userGameStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User game stats not found for user id: " + userId));

        if (statsDto.getLevel() != null) {
            stats.setLevel(statsDto.getLevel());
        }
        if (statsDto.getTitle() != null) {
            stats.setTitle(com.ma.ma_backend.domain.Title.valueOf(statsDto.getTitle()));
        }
        if (statsDto.getPowerPoints() != null) {
            stats.setPowerPoints(statsDto.getPowerPoints());
        }
        if (statsDto.getExperiencePoints() != null) {
            stats.setExperiencePoints(statsDto.getExperiencePoints());
        }
        if (statsDto.getCoins() != null) {
            stats.setCoins(statsDto.getCoins());
        }
        if (statsDto.getQrCode() != null) {
            stats.setQrCode(statsDto.getQrCode());
        }

        UserGameStats updatedStats = userGameStatsRepository.save(stats);
        UserGameStatsDto resultDto = entityMapper.userGameStatsToDto(updatedStats);
        resultDto.setBadgeCount(updatedStats.getEarnedBadges().size());
        return resultDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeDto> getBadgesByUserId(Long userId) {
        UserGameStats stats = userGameStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User game stats not found for user id: " + userId));

        return stats.getEarnedBadges().stream()
                .map(entityMapper::badgeToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public BadgeDto awardBadge(Long userId, Long badgeId) {
        UserGameStats stats = userGameStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User game stats not found for user id: " + userId));

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new NotFoundException("Badge not found with id: " + badgeId));

        if (!stats.getEarnedBadges().contains(badge)) {
            stats.getEarnedBadges().add(badge);
            userGameStatsRepository.save(stats);
        }

        return entityMapper.badgeToDto(badge);
    }

    @Override
    @Transactional
    public void removeBadge(Long userId, Long badgeId) {
        UserGameStats stats = userGameStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User game stats not found for user id: " + userId));

        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new NotFoundException("Badge not found with id: " + badgeId));

        stats.getEarnedBadges().remove(badge);
        userGameStatsRepository.save(stats);
    }
}
