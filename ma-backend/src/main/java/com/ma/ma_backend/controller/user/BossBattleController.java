package com.ma.ma_backend.controller.user;

import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.AttackRequest;
import com.ma.ma_backend.dto.AttackResponse;
import com.ma.ma_backend.dto.BossBattleDto;
import com.ma.ma_backend.dto.BossDto;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.service.intr.BossBattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boss-battle")
@RequiredArgsConstructor
public class BossBattleController {

    private final BossBattleService bossBattleService;
    private final UserRepository userRepository;

    @GetMapping("/next-boss")
    public ResponseEntity<BossDto> getNextBoss() {
        Long userId = getLoggedInUserId();
        BossDto boss = bossBattleService.getNextBoss(userId);
        return ResponseEntity.ok(boss);
    }

    @PostMapping("/start")
    public ResponseEntity<BossBattleDto> startBattle() {
        Long userId = getLoggedInUserId();
        BossBattleDto battle = bossBattleService.startBattle(userId);
        return ResponseEntity.ok(battle);
    }

    @PostMapping("/attack")
    public ResponseEntity<AttackResponse> attack(@RequestBody AttackRequest request) {
        Long userId = getLoggedInUserId();
        AttackResponse response = bossBattleService.attack(
            userId,
            request.getBattleId(),
            request.getActiveEquipmentIds() != null ?
                request.getActiveEquipmentIds().stream()
                    .filter(id -> id < 10000) // Assume weapon IDs are < 10000
                    .toList() : List.of(),
            request.getActiveEquipmentIds() != null ?
                request.getActiveEquipmentIds().stream()
                    .filter(id -> id >= 10000) // Assume clothing IDs are >= 10000
                    .toList() : List.of()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<BossBattleDto> getCurrentBattle() {
        Long userId = getLoggedInUserId();
        BossBattleDto battle = bossBattleService.getCurrentBattle(userId);
        if (battle == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(battle);
    }

    @GetMapping("/history")
    public ResponseEntity<List<BossBattleDto>> getBattleHistory() {
        Long userId = getLoggedInUserId();
        List<BossBattleDto> history = bossBattleService.getBattleHistory(userId);
        return ResponseEntity.ok(history);
    }

    private Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getId();
    }
}
