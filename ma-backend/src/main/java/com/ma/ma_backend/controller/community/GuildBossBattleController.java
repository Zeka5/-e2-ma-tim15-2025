package com.ma.ma_backend.controller.community;

import com.ma.ma_backend.dto.GuildBossBattleDto;
import com.ma.ma_backend.dto.GuildBossMissionProgressDto;
import com.ma.ma_backend.dto.GuildBossMissionSummaryDto;
import com.ma.ma_backend.service.intr.GuildBossBattleService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/guilds/{guildId}/boss-battle")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class GuildBossBattleController {

    private final GuildBossBattleService guildBossBattleService;
    private final UserService userService;

    /**
     * Pokreće specijalnu misiju za savez (samo vođa može)
     */
    @PostMapping("/start")
    public ResponseEntity<GuildBossBattleDto> startGuildBossBattle(@PathVariable Long guildId) {
        log.info("START GUILD BOSS BATTLE REQUEST - guildId: {}", guildId);
        Long userId = userService.getLogedInUser().getId();
        GuildBossBattleDto battle = guildBossBattleService.startGuildBossBattle(userId, guildId);
        return ResponseEntity.ok(battle);
    }

    /**
     * Vraća trenutnu aktivnu specijalnu misiju za savez
     */
    @GetMapping("/active")
    public ResponseEntity<GuildBossBattleDto> getActiveGuildBossBattle(@PathVariable Long guildId) {
        log.info("GET ACTIVE GUILD BOSS BATTLE REQUEST - guildId: {}", guildId);
        try {
            GuildBossBattleDto battle = guildBossBattleService.getActiveGuildBossBattle(guildId);
            log.info("Battle retrieved: {}", battle);
            if (battle == null) {
                log.info("No active battle found, returning 204");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(battle);
        } catch (Exception e) {
            log.error("ERROR in getActiveGuildBossBattle: ", e);
            throw e;
        }
    }

    /**
     * Vraća napredak svih članova u specijalnoj misiji
     */
    @GetMapping("/progress")
    public ResponseEntity<GuildBossMissionSummaryDto> getGuildBossBattleProgress(@PathVariable Long guildId) {
        GuildBossMissionSummaryDto summary = guildBossBattleService.getGuildBossBattleProgress(guildId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Vraća napredak pojedinačnog člana
     */
    @GetMapping("/progress/my")
    public ResponseEntity<GuildBossMissionProgressDto> getMyProgress(@PathVariable Long guildId) {
        Long userId = userService.getLogedInUser().getId();
        GuildBossMissionProgressDto progress = guildBossBattleService.getUserProgress(userId, guildId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Vraća napredak određenog člana
     */
    @GetMapping("/progress/user/{userId}")
    public ResponseEntity<GuildBossMissionProgressDto> getUserProgress(
            @PathVariable Long guildId,
            @PathVariable Long userId) {
        GuildBossMissionProgressDto progress = guildBossBattleService.getUserProgress(userId, guildId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Vraća istoriju specijalnih misija za savez
     */
    @GetMapping("/history")
    public ResponseEntity<List<GuildBossBattleDto>> getGuildBossBattleHistory(@PathVariable Long guildId) {
        List<GuildBossBattleDto> history = guildBossBattleService.getGuildBossBattleHistory(guildId);
        return ResponseEntity.ok(history);
    }
}
