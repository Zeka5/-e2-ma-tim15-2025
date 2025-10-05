package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.Guild;
import com.ma.ma_backend.domain.GuildInvite;
import com.ma.ma_backend.domain.GuildInviteStatus;
import com.ma.ma_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildInviteRepository extends JpaRepository<GuildInvite, Long> {
    @Query("SELECT gi FROM GuildInvite gi JOIN FETCH gi.guild JOIN FETCH gi.sender JOIN FETCH gi.receiver WHERE gi.id = :id")
    Optional<GuildInvite> findById(Long id);

    List<GuildInvite> findByReceiverAndStatus(User receiver, GuildInviteStatus status);
    boolean existsByGuildAndReceiverAndStatus(Guild guild, User receiver, GuildInviteStatus status);
}
