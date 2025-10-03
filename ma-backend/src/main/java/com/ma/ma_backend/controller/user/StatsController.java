package com.ma.ma_backend.controller.user;

import com.ma.ma_backend.dto.BadgeDto;
import com.ma.ma_backend.dto.UserGameStatsDto;
import com.ma.ma_backend.service.intr.UserGameStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final UserGameStatsService userGameStatsService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserGameStatsDto> getStatsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userGameStatsService.getStatsByUserId(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserGameStatsDto> updateStats(
            @PathVariable Long userId,
            @RequestBody UserGameStatsDto statsDto) {
        return ResponseEntity.ok(userGameStatsService.updateStats(userId, statsDto));
    }

    @GetMapping("/{userId}/badges")
    public ResponseEntity<List<BadgeDto>> getBadgesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userGameStatsService.getBadgesByUserId(userId));
    }

    @PostMapping("/{userId}/badges/{badgeId}")
    public ResponseEntity<BadgeDto> awardBadge(
            @PathVariable Long userId,
            @PathVariable Long badgeId) {
        return ResponseEntity.ok(userGameStatsService.awardBadge(userId, badgeId));
    }

    @DeleteMapping("/{userId}/badges/{badgeId}")
    public ResponseEntity<Void> removeBadge(
            @PathVariable Long userId,
            @PathVariable Long badgeId) {
        userGameStatsService.removeBadge(userId, badgeId);
        return ResponseEntity.noContent().build();
    }
}
