package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.UserBossProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBossProgressRepository extends JpaRepository<UserBossProgress, Long> {
    Optional<UserBossProgress> findByUserGameStatsIdAndBossId(Long userGameStatsId, Long bossId);

    List<UserBossProgress> findByUserGameStatsIdAndDefeatedFalse(Long userGameStatsId);

    @Query("SELECT ubp FROM UserBossProgress ubp WHERE ubp.userGameStats.id = :userGameStatsId " +
           "AND ubp.defeated = false ORDER BY ubp.boss.level ASC")
    List<UserBossProgress> findPendingBossesSorted(Long userGameStatsId);
}
