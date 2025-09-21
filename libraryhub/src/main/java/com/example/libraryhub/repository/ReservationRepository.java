package com.example.libraryhub.repository;

import com.example.libraryhub.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r JOIN FETCH r.book WHERE r.user.username = :username")
    List<Reservation> findWithBookByUser_Username(String username);
}