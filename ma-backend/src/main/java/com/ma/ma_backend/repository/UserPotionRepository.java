package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.UserGameStats;
import com.ma.ma_backend.domain.UserPotion;
import com.ma.ma_backend.domain.PotionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPotionRepository extends JpaRepository<UserPotion, Long> {
    List<UserPotion> findByUserGameStats(UserGameStats userGameStats);
    Optional<UserPotion> findByUserGameStatsAndPotionTemplate(UserGameStats userGameStats, PotionTemplate potionTemplate);
}
