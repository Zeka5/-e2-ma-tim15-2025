package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.UserClothing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserClothingRepository extends JpaRepository<UserClothing, Long> {
    List<UserClothing> findByUserGameStatsId(Long userGameStatsId);
}
