package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.BossBattle;
import com.ma.ma_backend.domain.BattleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BossBattleRepository extends JpaRepository<BossBattle, Long> {
    Optional<BossBattle> findByIdAndUserGameStatsId(Long id, Long userGameStatsId);

    List<BossBattle> findByUserGameStatsIdAndStatus(Long userGameStatsId, BattleStatus status);

    Optional<BossBattle> findByUserGameStatsIdAndBossIdAndStatus(
        Long userGameStatsId,
        Long bossId,
        BattleStatus status
    );

    List<BossBattle> findByUserGameStatsId(Long userGameStatsId);

    long countByUserGameStatsId(Long userGameStatsId);

    long countByUserGameStatsIdAndStatus(Long userGameStatsId, BattleStatus status);
}
