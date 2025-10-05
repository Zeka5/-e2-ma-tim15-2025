package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.FriendRequest;
import com.ma.ma_backend.domain.FriendRequestStatus;
import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.FriendRequestDto;
import com.ma.ma_backend.dto.UserDto;
import com.ma.ma_backend.exception.InvalidRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.FriendRequestRepository;
import com.ma.ma_backend.repository.UserRepository;
import com.ma.ma_backend.service.intr.FriendService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public FriendRequestDto sendFriendRequest(Long receiverId) {
        User sender = userService.getLogedInUser();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new InvalidRequestException("Cannot send friend request to yourself");
        }

        if (sender.getFriends().contains(receiver)) {
            throw new InvalidRequestException("Already friends with this user");
        }

        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendRequestStatus.PENDING)) {
            throw new InvalidRequestException("Friend request already sent");
        }

        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(receiver, sender, FriendRequestStatus.PENDING)) {
            throw new InvalidRequestException("This user has already sent you a friend request");
        }

        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        FriendRequest savedRequest = friendRequestRepository.save(request);
        return mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public FriendRequestDto acceptFriendRequest(Long requestId) {
        User currentUser = userService.getLogedInUser();
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Friend request not found"));

        if (!request.getReceiver().getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("You are not the receiver of this friend request");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new InvalidRequestException("Friend request is not pending");
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());

        User sender = userRepository.findByIdWithFriends(request.getSender().getId()).orElseThrow();
        User receiver = userRepository.findByIdWithFriends(request.getReceiver().getId()).orElseThrow();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userRepository.save(sender);
        userRepository.save(receiver);

        FriendRequest savedRequest = friendRequestRepository.save(request);
        return mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(Long requestId) {
        User currentUser = userService.getLogedInUser();
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Friend request not found"));

        if (!request.getReceiver().getId().equals(currentUser.getId())) {
            throw new InvalidRequestException("You are not the receiver of this friend request");
        }

        friendRequestRepository.delete(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getPendingRequests() {
        User currentUser = userService.getLogedInUser();
        List<FriendRequest> requests = friendRequestRepository
                .findPendingRequestsForUser(currentUser, FriendRequestStatus.PENDING);

        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getReceivedRequests() {
        User currentUser = userService.getLogedInUser();
        List<FriendRequest> requests = friendRequestRepository
                .findByReceiverAndStatus(currentUser, FriendRequestStatus.PENDING);

        return requests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getFriends() {
        User currentUser = userService.getLogedInUser();
        return currentUser.getFriends().stream()
                .map(entityMapper::mapUserToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFriend(Long friendId) {
        User currentUser = userService.getLogedInUser();
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!currentUser.getFriends().contains(friend)) {
            throw new InvalidRequestException("Not friends with this user");
        }

        currentUser.getFriends().remove(friend);
        friend.getFriends().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(friend);
    }

    private FriendRequestDto mapToDto(FriendRequest request) {
        FriendRequestDto dto = new FriendRequestDto();
        dto.setId(request.getId());
        dto.setSender(entityMapper.mapUserToDto(request.getSender()));
        dto.setReceiver(entityMapper.mapUserToDto(request.getReceiver()));
        dto.setStatus(request.getStatus().name());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setRespondedAt(request.getRespondedAt());
        return dto;
    }
}
