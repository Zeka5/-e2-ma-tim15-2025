package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.*;
import com.ma.ma_backend.dto.GuildDto;
import com.ma.ma_backend.dto.GuildInviteDto;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.exception.InvalidRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.GuildInviteRepository;
import com.ma.ma_backend.repository.GuildRepository;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.service.intr.GuildService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuildServiceImpl implements GuildService {
    private final GuildRepository guildRepository;
    private final GuildInviteRepository guildInviteRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public GuildDto createGuild(String name) {
        User leader = userService.getLogedInUser();

        if (leader.getCurrentGuild() != null) {
            throw new InvalidRequestException("You are already in a guild");
        }

        Guild guild = Guild.builder()
                .name(name)
                .leader(leader)
                .hasActiveMission(false)
                .members(new java.util.HashSet<>())
                .build();

        Guild savedGuild = guildRepository.save(guild);

        leader.setCurrentGuild(savedGuild);
        userRepository.save(leader);

        // Reload guild with members
        savedGuild = guildRepository.findById(savedGuild.getId()).orElseThrow();

        return mapToDto(savedGuild);
    }

    @Override
    @Transactional(readOnly = true)
    public GuildDto getGuildById(Long guildId) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));
        return mapToDto(guild);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuildDto> getAllGuilds() {
        List<Guild> guilds = guildRepository.findAll();
        return guilds.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuildDto> searchGuilds(String name) {
        List<Guild> guilds = guildRepository.findByNameContainingIgnoreCase(name);
        return guilds.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteGuild(Long guildId) {
        User currentUser = userService.getLogedInUser();
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));

        if (!guild.getLeader().getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("Only the guild leader can delete the guild");
        }

        if (guild.getHasActiveMission()) {
            throw new InvalidRequestException("Cannot delete guild with an active mission");
        }

        // Delete all pending invites for this guild
        guildInviteRepository.deleteByGuild(guild);

        // Remove guild from leader first
        User leader = guild.getLeader();
        leader.setCurrentGuild(null);
        userRepository.save(leader);

        // Remove guild from all members
        List<User> membersToUpdate = new java.util.ArrayList<>(guild.getMembers());
        for (User member : membersToUpdate) {
            if (!member.getId().equals(leader.getId())) { // Skip leader, already handled
                member.setCurrentGuild(null);
            }
        }
        userRepository.saveAll(membersToUpdate);
        userRepository.flush();

        // Now delete the guild
        guildRepository.delete(guild);
    }

    @Override
    @Transactional
    public GuildInviteDto inviteToGuild(Long guildId, Long userId) {
        User currentUser = userService.getLogedInUser();
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));
        User receiver = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!currentUser.getCurrentGuild().getId().equals(guildId)) {
            throw new InvalidRequestException("You are not a member of this guild");
        }

        if (!currentUser.getFriends().contains(receiver)) {
            throw new InvalidRequestException("You can only invite friends to the guild");
        }

        if (guildInviteRepository.existsByGuildAndReceiverAndStatus(guild, receiver, GuildInviteStatus.PENDING)) {
            throw new InvalidRequestException("Invite already sent to this user");
        }

        GuildInvite invite = GuildInvite.builder()
                .guild(guild)
                .sender(currentUser)
                .receiver(receiver)
                .status(GuildInviteStatus.PENDING)
                .build();

        GuildInvite savedInvite = guildInviteRepository.save(invite);
        return mapInviteToDto(savedInvite);
    }

    @Override
    @Transactional
    public GuildInviteDto acceptInvite(Long inviteId) {
        User currentUser = userService.getLogedInUser();
        GuildInvite invite = guildInviteRepository.findById(inviteId)
                .orElseThrow(() -> new NotFoundException("Guild invite not found"));

        if (!invite.getReceiver().getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("You are not the receiver of this invite");
        }

        if (invite.getStatus() != GuildInviteStatus.PENDING) {
            throw new InvalidRequestException("Invite is not pending");
        }

        // Leave current guild if in one
        if (currentUser.getCurrentGuild() != null) {
            Guild oldGuild = currentUser.getCurrentGuild();
            if (oldGuild.getHasActiveMission()) {
                throw new InvalidRequestException("Cannot leave guild with an active mission");
            }
            currentUser.setCurrentGuild(null);
        }

        invite.setStatus(GuildInviteStatus.ACCEPTED);
        invite.setRespondedAt(LocalDateTime.now());

        currentUser.setCurrentGuild(invite.getGuild());
        userRepository.save(currentUser);

        GuildInvite savedInvite = guildInviteRepository.save(invite);
        return mapInviteToDto(savedInvite);
    }

    @Override
    @Transactional
    public void rejectInvite(Long inviteId) {
        User currentUser = userService.getLogedInUser();
        GuildInvite invite = guildInviteRepository.findById(inviteId)
                .orElseThrow(() -> new NotFoundException("Guild invite not found"));

        if (!invite.getReceiver().getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("You are not the receiver of this invite");
        }

        guildInviteRepository.delete(invite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuildInviteDto> getPendingInvites() {
        User currentUser = userService.getLogedInUser();
        List<GuildInvite> invites = guildInviteRepository
                .findByReceiverAndStatus(currentUser, GuildInviteStatus.PENDING);

        return invites.stream()
                .map(this::mapInviteToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void leaveGuild() {
        User currentUser = userService.getLogedInUser();

        if (currentUser.getCurrentGuild() == null) {
            throw new InvalidRequestException("You are not in a guild");
        }

        Guild guild = currentUser.getCurrentGuild();

        if (guild.getLeader().getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("Guild leader cannot leave. Delete the guild instead.");
        }

        if (guild.getHasActiveMission()) {
            throw new InvalidRequestException("Cannot leave guild with an active mission");
        }

        currentUser.setCurrentGuild(null);
        userRepository.save(currentUser);
    }

    private GuildDto mapToDto(Guild guild) {
        GuildDto dto = new GuildDto();
        dto.setId(guild.getId());
        dto.setName(guild.getName());
        dto.setLeader(entityMapper.mapUserToDto(guild.getLeader()));
        dto.setMembers(guild.getMembers().stream()
                .map(entityMapper::mapUserToDto)
                .collect(Collectors.toList()));
        dto.setHasActiveMission(guild.getHasActiveMission());
        dto.setCreatedAt(guild.getCreatedAt());
        return dto;
    }

    private GuildInviteDto mapInviteToDto(GuildInvite invite) {
        GuildInviteDto dto = new GuildInviteDto();
        dto.setId(invite.getId());
        dto.setGuild(mapToDto(invite.getGuild()));
        dto.setSender(entityMapper.mapUserToDto(invite.getSender()));
        dto.setReceiver(entityMapper.mapUserToDto(invite.getReceiver()));
        dto.setStatus(invite.getStatus().name());
        dto.setCreatedAt(invite.getCreatedAt());
        dto.setRespondedAt(invite.getRespondedAt());
        return dto;
    }
}
