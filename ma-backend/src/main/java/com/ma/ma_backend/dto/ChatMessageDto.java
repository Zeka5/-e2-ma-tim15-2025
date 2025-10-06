package com.ma.ma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long guildId;
    private Long senderId;
    private String senderUsername;
    private Integer senderAvatarId;
    private String content;
    private LocalDateTime createdAt;
}
