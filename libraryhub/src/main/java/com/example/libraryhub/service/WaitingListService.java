package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.User;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.WaitingListRepository;
import com.example.libraryhub.repository.ReservationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaitingListService {

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Add current user to the waiting list of the given book.
     * Throws IllegalStateException if the user already reserved the book
     * or is already in the waiting list.
     */
    @Transactional
    public WaitingList addToWaitingList(Long bookId, String username) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // User already holds the reservation of this book
        boolean alreadyReserved = reservationRepository.findByBookIdWithUser(bookId)
                .map(res -> res.getUser().getId().equals(user.getId()))
                .orElse(false);
        if (alreadyReserved) {
            throw new IllegalStateException("You have already reserved this book, cannot join waiting list.");
        }

        // User already in waiting list
        if (waitingListRepository.existsByBookAndUser(book, user)) {
            throw new IllegalStateException("You are already on the waiting list for this book.");
        }

        // Create and persist entry
        WaitingList entry = new WaitingList();
        entry.setBook(book);
        entry.setUser(user);
        entry.setJoinDate(LocalDateTime.now());

        try {
            return waitingListRepository.save(entry);
        } catch (DataIntegrityViolationException e) {
            // In case of DB-level uniqueness constraint on (book_id, user_id)
            throw new IllegalStateException("You are already on the waiting list for this book.");
        }
    }

    /**
     * Get waiting list entries of a book ordered by joinDate ascending.
     */
    public List<WaitingList> getWaitingList(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return waitingListRepository.findByBookOrderByJoinDateAsc(book);
    }

    /**
     * Check whether the given user is in the waiting list of the given book.
     */
    public boolean isUserInWaitingList(Long bookId, String username) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return waitingListRepository.existsByBookAndUser(book, user);
    }

    /**
     * Cancel current user's waiting entry for the given book (if exists).
     */
    @Transactional
    public void cancelWaiting(Long bookId, String username) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        waitingListRepository.deleteByBookAndUser(book, user);
    }

    /**
     * Admin-only: delete a waiting list entry by id.
     * This is used by AdminController when removing arbitrary entries.
     */
    @Transactional
    public void deleteWaitingEntry(Long entryId) {
        waitingListRepository.deleteById(entryId);
    }
}
