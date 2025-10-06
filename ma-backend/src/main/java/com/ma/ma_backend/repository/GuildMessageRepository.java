package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.Guild;
import com.ma.ma_backend.domain.GuildMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuildMessageRepository extends JpaRepository<GuildMessage, Long> {
    @Query("SELECT gm FROM GuildMessage gm JOIN FETCH gm.sender WHERE gm.guild = :guild ORDER BY gm.createdAt DESC")
    List<GuildMessage> findByGuildOrderByCreatedAtDesc(Guild guild);

    void deleteByGuild(Guild guild);
}
