package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {
    @Query("SELECT g FROM Guild g LEFT JOIN FETCH g.members WHERE g.id = :id")
    Optional<Guild> findById(Long id);

    List<Guild> findByNameContainingIgnoreCase(String name);
}
