package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository; // Assuming this repository exists

    @Transactional
    public Reservation createReservation(Long bookId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for reservation.");
        }

        book.setAvailable(false);

        Reservation reservation = new Reservation(user, book);
        return reservationRepository.save(reservation);
    }

    /**
     * Finds all reservations for a given user.
     * 
     * @param username The username of the user.
     * @return A list of reservations with associated book data.
     */
    public List<Reservation> getReservationsForUser(String username) {
        return reservationRepository.findWithBookByUser_Username(username);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllWithDetails();
    }

    public Optional<Reservation> getReservationForBook(Long bookId) {
        return reservationRepository.findByBookIdWithUser(bookId);
    }
}