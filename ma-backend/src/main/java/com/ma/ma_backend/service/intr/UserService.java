package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.PublicUserProfileDto;
import com.ma.ma_backend.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    User getLogedInUser();
    UserDto getMyInfo();
    UserDto updateMyInfo(UserDto userDto);
    void delete(Long userId);

    PublicUserProfileDto getPublicProfile(Long userId);
    List<PublicUserProfileDto> searchUsers(String username);
}
