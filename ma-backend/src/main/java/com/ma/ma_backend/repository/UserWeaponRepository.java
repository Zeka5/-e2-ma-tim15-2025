package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.UserWeapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWeaponRepository extends JpaRepository<UserWeapon, Long> {
    List<UserWeapon> findByUserGameStatsId(Long userGameStatsId);
}
