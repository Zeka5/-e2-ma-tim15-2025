package com.ma.ma_backend.controller.community;

import com.ma.ma_backend.dto.ChatMessageDto;
import com.ma.ma_backend.dto.SendMessageRequest;
import com.ma.ma_backend.service.intr.GuildChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GuildChatController {
    private final GuildChatService guildChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request) {
        ChatMessageDto message = guildChatService.sendMessageFromWebSocket(
                request.getGuildId(),
                request.getSenderId(),
                request.getContent()
        );
        messagingTemplate.convertAndSend("/topic/guild/" + request.getGuildId(), message);
    }

    @GetMapping("/api/guilds/{guildId}/messages")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDto>> getGuildMessages(@PathVariable Long guildId) {
        return ResponseEntity.ok(guildChatService.getGuildMessages(guildId));
    }
}
