package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.Boss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BossRepository extends JpaRepository<Boss, Long> {
    Optional<Boss> findByLevel(Integer level);
}
