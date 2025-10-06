package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.ClothingTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothingTemplateRepository extends JpaRepository<ClothingTemplate, Long> {
}
