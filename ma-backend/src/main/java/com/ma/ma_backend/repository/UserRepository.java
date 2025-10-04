package com.ma.ma_backend.repository;

import com.ma.ma_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.gameStats WHERE u.id = :id")
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.gameStats WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByActivationToken(String activationToken);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.gameStats gs LEFT JOIN FETCH gs.earnedBadges WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findByUsernameContainingIgnoreCase(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends WHERE u.id = :id")
    Optional<User> findByIdWithFriends(Long id);
}
