package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.PublicUserProfileDto;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.exception.EntityExistsException;
import com.ma.ma_backend.exception.InvalidRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.directory.InvalidAttributesException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(entityMapper::mapUserToDto)
                .toList();

    }

    @Override
    public User getLogedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDto getMyInfo() {
        User user = getLogedInUser();
        return entityMapper.mapUserToDto(user);
    }

    @Override
    public UserDto updateMyInfo(UserDto userDto) {
        User user = getLogedInUser();

        if (userDto.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(userDto.getEmail())) {
            throw new EntityExistsException("Email is already in use.");
        }

        if(userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if(userDto.getAvatarId() != null) {
            user.setAvatarId(userDto.getAvatarId());
        }

        User saved = userRepository.save(user);
        return entityMapper.mapUserToDto(saved);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = getLogedInUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new EntityExistsException("Old password does not match.");
        }
        if (user.getPassword().equalsIgnoreCase(oldPassword)) {
            throw new InvalidRequestException("Please enter different password than the old one.");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Override
    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicUserProfileDto getPublicProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return entityMapper.userToPublicProfile(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicUserProfileDto> searchUsers(String username) {
        User currentUser = getLogedInUser();
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .map(entityMapper::userToPublicProfile)
                .collect(Collectors.toList());
    }

}
