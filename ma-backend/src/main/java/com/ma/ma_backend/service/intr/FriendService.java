package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.FriendRequestDto;
import com.ma.ma_backend.dto.UserDto;

import java.util.List;

public interface FriendService {
    FriendRequestDto sendFriendRequest(Long receiverId);
    FriendRequestDto acceptFriendRequest(Long requestId);
    void rejectFriendRequest(Long requestId);
    List<FriendRequestDto> getPendingRequests();
    List<FriendRequestDto> getReceivedRequests();
    List<UserDto> getFriends();
    void removeFriend(Long friendId);
}
