package com.ma.ma_backend.controller.community;

import com.ma.ma_backend.dto.GuildDto;
import com.ma.ma_backend.dto.GuildInviteDto;
import com.ma.ma_backend.service.intr.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildController {
    private final GuildService guildService;

    @PostMapping
    public ResponseEntity<GuildDto> createGuild(@RequestParam String name) {
        try {
            System.out.println("Creating guild with name: " + name);
            GuildDto guildDto = guildService.createGuild(name);
            System.out.println(guildDto.getName()+" CREATED");
            System.out.println("Guild DTO: " + guildDto);
            return ResponseEntity.ok(guildDto);
        } catch (Exception e) {
            System.err.println("ERROR creating guild: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<GuildDto>> getAllGuilds() {
        return ResponseEntity.ok(guildService.getAllGuilds());
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuildDto>> searchGuilds(@RequestParam String name) {
        return ResponseEntity.ok(guildService.searchGuilds(name));
    }

    @GetMapping("/{guildId}")
    public ResponseEntity<GuildDto> getGuildById(@PathVariable Long guildId) {
        return ResponseEntity.ok(guildService.getGuildById(guildId));
    }

    @DeleteMapping("/{guildId}")
    public ResponseEntity<Void> deleteGuild(@PathVariable Long guildId) {
        guildService.deleteGuild(guildId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{guildId}/invite/{userId}")
    public ResponseEntity<GuildInviteDto> inviteToGuild(
            @PathVariable Long guildId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(guildService.inviteToGuild(guildId, userId));
    }

    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<GuildInviteDto> acceptInvite(@PathVariable Long inviteId) {
        return ResponseEntity.ok(guildService.acceptInvite(inviteId));
    }

    @DeleteMapping("/invites/{inviteId}/reject")
    public ResponseEntity<Void> rejectInvite(@PathVariable Long inviteId) {
        guildService.rejectInvite(inviteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/invites/pending")
    public ResponseEntity<List<GuildInviteDto>> getPendingInvites() {
        return ResponseEntity.ok(guildService.getPendingInvites());
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveGuild() {
        guildService.leaveGuild();
        return ResponseEntity.noContent().build();
    }
}
