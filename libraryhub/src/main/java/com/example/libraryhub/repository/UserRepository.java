package com.example.libraryhub.repository;

import com.example.libraryhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** JPA repository for User aggregate. */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE users SET role = :role WHERE id = :id", nativeQuery = true)
    void updateUserRole(@Param("id") Long id, @Param("role") String role);
}
