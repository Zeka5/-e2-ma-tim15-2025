package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.BattleStatus;
import com.ma.ma_backend.domain.GuildBossBattle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildBossBattleRepository extends JpaRepository<GuildBossBattle, Long> {

    Optional<GuildBossBattle> findByGuildIdAndStatus(Long guildId, BattleStatus status);

    List<GuildBossBattle> findByGuildIdOrderByStartedAtDesc(Long guildId);

    @Query("SELECT gbb FROM GuildBossBattle gbb WHERE gbb.guild.id = :guildId AND gbb.status = 'IN_PROGRESS'")
    Optional<GuildBossBattle> findActiveGuildBattle(@Param("guildId") Long guildId);

    @Query("SELECT COUNT(gbb) > 0 FROM GuildBossBattle gbb WHERE gbb.guild.id = :guildId AND gbb.status = 'IN_PROGRESS'")
    boolean hasActiveGuildBattle(@Param("guildId") Long guildId);
}
