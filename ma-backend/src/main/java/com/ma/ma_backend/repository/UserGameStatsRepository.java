package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.UserGameStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGameStatsRepository extends JpaRepository<UserGameStats, Long> {
    Optional<UserGameStats> findByUserId(Long userId);
}
