package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.WeaponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeaponTemplateRepository extends JpaRepository<WeaponTemplate, Long> {
}
