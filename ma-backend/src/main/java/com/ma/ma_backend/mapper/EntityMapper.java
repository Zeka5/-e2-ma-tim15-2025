package com.ma.ma_backend.mapper;

import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public UserDto mapUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().name());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setAvatarId(user.getAvatarId());
        return userDto;
    }
}
