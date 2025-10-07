package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.GuildBossBattleDto;
import com.ma.ma_backend.dto.GuildBossMissionProgressDto;
import com.ma.ma_backend.dto.GuildBossMissionSummaryDto;

import java.util.List;

public interface GuildBossBattleService {

    /**
     * Pokreće specijalnu misiju za savez (samo vođa može)
     */
    GuildBossBattleDto startGuildBossBattle(Long userId, Long guildId);

    /**
     * Vraća trenutnu aktivnu specijalnu misiju za savez
     */
    GuildBossBattleDto getActiveGuildBossBattle(Long guildId);

    /**
     * Vraća napredak svih članova u specijalnoj misiji
     */
    GuildBossMissionSummaryDto getGuildBossBattleProgress(Long guildId);

    /**
     * Vraća napredak pojedinačnog člana
     */
    GuildBossMissionProgressDto getUserProgress(Long userId, Long guildId);

    /**
     * Ažurira napredak kada korisnik kupi nešto u prodavnici
     */
    void onShopPurchase(Long userId);

    /**
     * Ažurira napredak kada korisnik uspešno pogodi bosa u regularnoj borbi
     */
    void onBossHit(Long userId);

    /**
     * Ažurira napredak kada korisnik završi zadatak
     */
    void onTaskCompleted(Long userId, String difficulty, String importance);

    /**
     * Ažurira napredak kada korisnik pošalje poruku u savezu
     */
    void onGuildMessage(Long userId, Long guildId);

    /**
     * Proverava i završava istekle specijalne misije
     */
    void checkAndCompleteExpiredMissions();

    /**
     * Vraća istoriju specijalnih misija za savez
     */
    List<GuildBossBattleDto> getGuildBossBattleHistory(Long guildId);
}
