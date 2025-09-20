package com.example.libraryhub.repository;

import com.example.libraryhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** JPA repository for User aggregate. */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
