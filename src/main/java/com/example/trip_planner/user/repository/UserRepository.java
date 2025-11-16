package com.example.trip_planner.user.repository;

import com.example.trip_planner.user.model.User;
import com.example.trip_planner.user.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLocalId(String localId);
    
    Optional<User> findByEmail(String email);
    
    Page<User> findByStatus(UserStatus status, Pageable pageable);
}
