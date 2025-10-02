package com.example.libraryhub.repository;

import com.example.libraryhub.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r JOIN FETCH r.book WHERE r.user.username = :username")
    List<Reservation> findWithBookByUser_Username(String username);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user WHERE r.book.id = :bookId")
    Optional<Reservation> findByBookIdWithUser(Long bookId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.book ORDER BY r.reservationDate DESC")
    List<Reservation> findAllWithDetails();
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation r WHERE r.book.id = :bookId")
    void deleteByBookId(Long bookId);
}