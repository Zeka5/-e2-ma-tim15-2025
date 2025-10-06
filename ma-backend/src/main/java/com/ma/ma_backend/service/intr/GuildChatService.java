package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.ChatMessageDto;

import java.util.List;

public interface GuildChatService {
    ChatMessageDto sendMessage(Long guildId, String content);
    ChatMessageDto sendMessageFromWebSocket(Long guildId, Long senderId, String content);
    List<ChatMessageDto> getGuildMessages(Long guildId);
}
