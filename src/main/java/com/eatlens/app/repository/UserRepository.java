package com.eatlens.app.repository;

import com.eatlens.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.isDeleted = false")
    Optional<User> findActiveUserByEmail(String email);
}
