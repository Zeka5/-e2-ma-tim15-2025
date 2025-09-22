package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.AuthData;
import com.ma.ma_backend.dto.LoginRequest;
import com.ma.ma_backend.dto.UserDto;

public interface AuthService {
    UserDto register(UserDto userDto);
    AuthData login(LoginRequest loginRequest);
    String activateAccount(String activationToken);
}
