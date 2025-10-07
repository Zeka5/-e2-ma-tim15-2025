package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.Guild;
import com.ma.ma_backend.domain.GuildMessage;
import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.ChatMessageDto;
import com.ma.ma_backend.exception.InvalidRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.repository.GuildMessageRepository;
import com.ma.ma_backend.repository.GuildRepository;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.service.intr.GuildBossBattleService;
import com.ma.ma_backend.service.intr.GuildChatService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuildChatServiceImpl implements GuildChatService {
    private final GuildMessageRepository guildMessageRepository;
    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final @Lazy GuildBossBattleService guildBossBattleService;

    @Override
    @Transactional
    public ChatMessageDto sendMessage(Long guildId, String content) {
        User currentUser = userService.getLogedInUser();
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));

        if (currentUser.getCurrentGuild() == null || !currentUser.getCurrentGuild().getId().equals(guildId)) {
            throw new InvalidRequestException("You are not a member of this guild");
        }

        GuildMessage message = GuildMessage.builder()
                .guild(guild)
                .sender(currentUser)
                .content(content)
                .build();

        GuildMessage savedMessage = guildMessageRepository.save(message);

        // Trigger guild boss battle progress
        guildBossBattleService.onGuildMessage(currentUser.getId(), guildId);

        return mapToDto(savedMessage);
    }

    @Override
    @Transactional
    public ChatMessageDto sendMessageFromWebSocket(Long guildId, Long senderId, String content) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (sender.getCurrentGuild() == null || !sender.getCurrentGuild().getId().equals(guildId)) {
            throw new InvalidRequestException("You are not a member of this guild");
        }

        GuildMessage message = GuildMessage.builder()
                .guild(guild)
                .sender(sender)
                .content(content)
                .build();

        GuildMessage savedMessage = guildMessageRepository.save(message);

        // Trigger guild boss battle progress
        guildBossBattleService.onGuildMessage(senderId, guildId);

        return mapToDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getGuildMessages(Long guildId) {
        User currentUser = userService.getLogedInUser();
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));

        if (currentUser.getCurrentGuild() == null || !currentUser.getCurrentGuild().getId().equals(guildId)) {
            throw new InvalidRequestException("You are not a member of this guild");
        }

        List<GuildMessage> messages = guildMessageRepository.findByGuildOrderByCreatedAtDesc(guild);
        return messages.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ChatMessageDto mapToDto(GuildMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .guildId(message.getGuild().getId())
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .senderAvatarId(message.getSender().getAvatarId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
