package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserId(Long userId);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    boolean existsByColorAndUserId(String color, Long userId);
    boolean existsByColorAndUserIdAndIdNot(String color, Long userId, Long categoryId);
}
