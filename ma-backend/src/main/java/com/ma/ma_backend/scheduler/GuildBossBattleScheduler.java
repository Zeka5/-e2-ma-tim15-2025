package com.ma.ma_backend.scheduler;

import com.ma.ma_backend.service.intr.GuildBossBattleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuildBossBattleScheduler {

    private final GuildBossBattleService guildBossBattleService;

    /**
     * Proverava i završava istekle specijalne misije svakih sat vremena
     */
    @Scheduled(cron = "0 0 * * * *") // Svaki sat na 0 minuta
    public void checkExpiredMissions() {
        log.info("Checking for expired guild boss battles...");
        try {
            guildBossBattleService.checkAndCompleteExpiredMissions();
            log.info("Expired guild boss battles check completed successfully");
        } catch (Exception e) {
            log.error("Error while checking expired guild boss battles", e);
        }
    }
}
