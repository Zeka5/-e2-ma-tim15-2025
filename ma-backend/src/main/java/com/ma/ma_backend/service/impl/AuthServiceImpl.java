package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.domain.UserRole;
import com.ma.ma_backend.dto.AuthData;
import com.ma.ma_backend.dto.LoginRequest;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.exception.EntityExistsException;
import com.ma.ma_backend.exception.InvalidCredentialsException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.security.jwt.JwtUtils;
import com.ma.ma_backend.service.intr.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;
    private final JwtUtils jwtUtils;

    @Override
    public UserDto register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EntityExistsException("Email is already in use.");
        }
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new EntityExistsException("Username is already in use.");
        }

        UserRole role = UserRole.USER;

        if(userDto.getAvatarId() == null){
            userDto.setAvatarId(1);
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(role)
                .avatarId(userDto.getAvatarId())
                .build();
        User savedUser = userRepository.save(user);

        return entityMapper.mapUserToDto(savedUser);
    }

    @Override
    public AuthData login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User with provided email not found"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Wrong password");
        }
        String token = jwtUtils.generateTokenWithUserInfo(user);
        return AuthData.builder()
                .user(entityMapper.mapUserToDto(user))
                .token(token)
                .expirationDate(jwtUtils.getExpirationDate())
                .build();
    }
}
