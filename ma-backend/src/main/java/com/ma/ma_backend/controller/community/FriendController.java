package com.ma.ma_backend.controller.community;

import com.ma.ma_backend.dto.FriendRequestDto;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.service.intr.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/request/{receiverId}")
    public ResponseEntity<FriendRequestDto> sendFriendRequest(@PathVariable Long receiverId) {
        return ResponseEntity.ok(friendService.sendFriendRequest(receiverId));
    }

    @PostMapping("/request/{requestId}/accept")
    public ResponseEntity<FriendRequestDto> acceptFriendRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.acceptFriendRequest(requestId));
    }

    @DeleteMapping("/request/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<FriendRequestDto>> getPendingRequests() {
        return ResponseEntity.ok(friendService.getPendingRequests());
    }

    @GetMapping("/requests/received")
    public ResponseEntity<List<FriendRequestDto>> getReceivedRequests() {
        return ResponseEntity.ok(friendService.getReceivedRequests());
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long friendId) {
        friendService.removeFriend(friendId);
        return ResponseEntity.noContent().build();
    }
}
