package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.PotionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PotionTemplateRepository extends JpaRepository<PotionTemplate, Long> {
}
