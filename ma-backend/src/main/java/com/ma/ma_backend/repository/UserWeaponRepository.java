package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.UserGameStats;
import com.ma.ma_backend.domain.UserWeapon;
import com.ma.ma_backend.domain.WeaponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWeaponRepository extends JpaRepository<UserWeapon, Long> {
    List<UserWeapon> findByUserGameStatsId(Long userGameStatsId);
    Optional<UserWeapon> findByUserGameStatsAndWeaponTemplate(UserGameStats userGameStats, WeaponTemplate weaponTemplate);
}
