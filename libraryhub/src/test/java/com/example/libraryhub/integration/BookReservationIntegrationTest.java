package com.example.libraryhub.integration;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookReservationIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Book testBook;
    private Customer testUser;

    @BeforeEach
    void setUp() {
        // Clear repositories
        reservationRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        // Create test data
        testBook = new Book("Integration Test Book", "Test Author", "Fiction");
        testBook.setAvailable(true);
        testBook = bookRepository.save(testBook);

        testUser = new Customer(null, "integrationuser", "integration@example.com", "hashedPassword");
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCompleteBookReservationFlow() {
        // 1. Verify book is available
        assertTrue(testBook.isAvailable());

        // 2. Create reservation
        Reservation reservation = reservationService.createReservation(testBook.getId(), testUser.getUsername());
        assertNotNull(reservation);
        assertNotNull(reservation.getId());

        // 3. Verify book is now unavailable
        Book updatedBook = bookRepository.findById(testBook.getId()).orElseThrow();
        assertFalse(updatedBook.isAvailable());

        // 4. Get user's reservations
        var userReservations = reservationService.getReservationsForUser(testUser.getUsername());
        assertEquals(1, userReservations.size());

        // 5. Return the book
        reservationService.returnBook(testBook.getId(), testUser.getUsername());

        // 6. Verify book is available again
        Book returnedBook = bookRepository.findById(testBook.getId()).orElseThrow();
        assertTrue(returnedBook.isAvailable());

        // 7. Verify reservation is deleted
        var reservationsAfterReturn = reservationService.getReservationsForUser(testUser.getUsername());
        assertEquals(0, reservationsAfterReturn.size());
    }

    @Test
    void testCannotReserveUnavailableBook() {
        // Make book unavailable
        testBook.setAvailable(false);
        bookRepository.save(testBook);

        // Try to reserve
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(testBook.getId(), testUser.getUsername());
        });
    }

    @Test
    void testBookSearchByTitle() {
        Book anotherBook = new Book("Another Integration Test", "Another Author", "Drama");
        bookRepository.save(anotherBook);

        var foundBooks = bookService.searchByTitle("Integration");
        assertTrue(foundBooks.size() >= 2);
    }

    @Test
    void testDeleteBookCascade() {
        // Create reservation
        reservationService.createReservation(testBook.getId(), testUser.getUsername());

        // Delete book (should delete reservation too)
        bookService.deleteBook(testBook.getId());

        // Verify book is deleted
        assertFalse(bookRepository.findById(testBook.getId()).isPresent());

        // Verify reservation is deleted
        assertEquals(0, reservationRepository.findAll().size());
    }
}
