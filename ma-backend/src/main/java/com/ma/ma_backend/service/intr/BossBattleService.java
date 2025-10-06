package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.AttackResponse;
import com.ma.ma_backend.dto.BossBattleDto;
import com.ma.ma_backend.dto.BossDto;
import com.ma.ma_backend.dto.BattleStatsPreviewDto;

import java.util.List;

public interface BossBattleService {
    /**
     * Get the next boss that the user needs to fight
     */
    BossDto getNextBoss(Long userId);

    /**
     * Start a new battle with the next boss
     */
    BossBattleDto startBattle(Long userId);

    /**
     * Execute an attack on the current battle
     */
    AttackResponse attack(Long userId, Long battleId, List<Long> activeWeaponIds, List<Long> activeClothingIds);

    /**
     * Get the current active battle for a user
     */
    BossBattleDto getCurrentBattle(Long userId);

    /**
     * Get battle history for a user
     */
    List<BossBattleDto> getBattleHistory(Long userId);

    /**
     * Called when user completes a level to make the next boss available
     */
    void onLevelComplete(Long userId, Integer newLevel);

    /**
     * Get battle stats preview (PP, success rate, max attacks) for displaying before battle starts
     */
    BattleStatsPreviewDto getBattleStatsPreview(Long userId);
}
