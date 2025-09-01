package com.ma.ma_backend.controller;

import com.ma.ma_backend.dto.AuthData;
import com.ma.ma_backend.dto.LoginRequest;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.service.intr.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(authService.register(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthData> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
