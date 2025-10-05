package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.GuildDto;
import com.ma.ma_backend.dto.GuildInviteDto;

import java.util.List;

public interface GuildService {
    GuildDto createGuild(String name);
    GuildDto getGuildById(Long guildId);
    List<GuildDto> getAllGuilds();
    List<GuildDto> searchGuilds(String name);
    void deleteGuild(Long guildId);
    GuildInviteDto inviteToGuild(Long guildId, Long userId);
    GuildInviteDto acceptInvite(Long inviteId);
    void rejectInvite(Long inviteId);
    List<GuildInviteDto> getPendingInvites();
    void leaveGuild();
}
