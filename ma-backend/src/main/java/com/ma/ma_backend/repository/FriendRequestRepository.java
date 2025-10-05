package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.FriendRequest;
import com.ma.ma_backend.domain.FriendRequestStatus;
import com.ma.ma_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver WHERE fr.id = :id")
    Optional<FriendRequest> findById(Long id);

    @Query("SELECT fr FROM FriendRequest fr WHERE (fr.receiver = :user OR fr.sender = :user) AND fr.status = :status")
    List<FriendRequest> findPendingRequestsForUser(User user, FriendRequestStatus status);

    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequestStatus status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendRequestStatus status);
}
