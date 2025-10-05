package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.User;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.WaitingListRepository;
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
    private final WaitingListRepository waitingListRepository;

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

    // Handle book return and auto-assign to the earliest waiting user if any (Plan B: null-check)
    @Transactional
    public void returnBook(Long bookId, String username) {
        // 1) Find current active reservation for this book
        Reservation current = getReservationForBook(bookId)
                .orElseThrow(() -> new RuntimeException("No active reservation for book: " + bookId));

        // 2) Ensure the requester is the holder of this reservation
        if (!current.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not hold this reservation.");
        }

        Book book = current.getBook();

        // 3) Remove current reservation
        reservationRepository.delete(current);

        // 4) Pick the earliest waiting list entry (null if none)
        WaitingList first = waitingListRepository.findFirstByBookIdOrderByJoinDateAsc(bookId);

        if (first != null) {
            // 4a) Assign to the next user in waiting list
            User nextUser = first.getUser();

            // Remove the waiting list entry
            waitingListRepository.delete(first);

            // Create a new reservation for the next user
            Reservation nextReservation = new Reservation(nextUser, book);
            reservationRepository.save(nextReservation);

            // Keep the book unavailable since it is now reserved again
            book.setAvailable(false);
        } else {
            // 4b) No one is waiting → mark book as available
            book.setAvailable(true);
        }

        // 5) Persist book availability change
        bookRepository.save(book);
    }
}
