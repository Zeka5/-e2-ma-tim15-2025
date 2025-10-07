package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.GuildMessageDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GuildMessageDateRepository extends JpaRepository<GuildMessageDate, Long> {

    @Query("SELECT gmd FROM GuildMessageDate gmd WHERE gmd.missionProgress.id = :progressId AND gmd.messageDate = :date")
    Optional<GuildMessageDate> findByProgressIdAndDate(@Param("progressId") Long progressId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(gmd) FROM GuildMessageDate gmd WHERE gmd.missionProgress.id = :progressId")
    long countDaysWithMessages(@Param("progressId") Long progressId);
}
