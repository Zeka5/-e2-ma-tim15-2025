package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.GuildBossMissionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildBossMissionProgressRepository extends JpaRepository<GuildBossMissionProgress, Long> {

    Optional<GuildBossMissionProgress> findByGuildBossBattleIdAndUserId(Long guildBossBattleId, Long userId);

    List<GuildBossMissionProgress> findByGuildBossBattleId(Long guildBossBattleId);

    @Query("SELECT gmp FROM GuildBossMissionProgress gmp WHERE gmp.guildBossBattle.id = :battleId ORDER BY gmp.totalDamageDealt DESC")
    List<GuildBossMissionProgress> findByBattleIdOrderByDamageDesc(@Param("battleId") Long battleId);

    @Query("SELECT gmp FROM GuildBossMissionProgress gmp " +
           "JOIN gmp.guildBossBattle gbb " +
           "WHERE gbb.guild.id = :guildId AND gbb.status = 'IN_PROGRESS' AND gmp.user.id = :userId")
    Optional<GuildBossMissionProgress> findActiveProgressForUser(@Param("guildId") Long guildId, @Param("userId") Long userId);
}
