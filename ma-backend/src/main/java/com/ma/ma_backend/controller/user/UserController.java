package com.ma.ma_backend.controller.user;

import com.ma.ma_backend.dto.ChangePasswordRequest;
import com.ma.ma_backend.dto.PublicUserProfileDto;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyInfo(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateMyInfo(userDto));
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        return ResponseEntity.ok("Password updated");
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("Successfully deleted user");
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<PublicUserProfileDto> getPublicProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getPublicProfile(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PublicUserProfileDto>> searchUsers(@RequestParam String username) {
        return ResponseEntity.ok(userService.searchUsers(username));
    }
}
