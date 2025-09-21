package com.example.libraryhub.repository;

import com.example.libraryhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

/** JPA repository for User aggregate. */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reservations r LEFT JOIN FETCH r.book WHERE TYPE(u) = Customer ORDER BY u.username")
    List<User> findAllCustomersWithReservations();
}
