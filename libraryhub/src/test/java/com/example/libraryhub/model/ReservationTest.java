package com.example.libraryhub.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class ReservationTest {

    private Reservation reservation;
    private Customer user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new Customer(1L, "testuser", "test@example.com", "hashedPassword");
        book = new Book("Test Book", "Test Author", "Fiction");
        reservation = new Reservation(user, book);
    }

    @Test
    void testReservationConstructor() {
        assertEquals(user, reservation.getUser());
        assertEquals(book, reservation.getBook());
        assertNotNull(reservation.getReservationDate());
        assertTrue(reservation.getReservationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testNoArgsConstructor() {
        Reservation emptyReservation = new Reservation();
        assertNotNull(emptyReservation);
    }

    @Test
    void testSetters() {
        Customer newUser = new Customer(2L, "newuser", "new@example.com", "newHash");
        Book newBook = new Book("New Book", "New Author", "Drama");
        LocalDateTime newDate = LocalDateTime.now().minusDays(1);

        reservation.setUser(newUser);
        reservation.setBook(newBook);
        reservation.setReservationDate(newDate);

        assertEquals(newUser, reservation.getUser());
        assertEquals(newBook, reservation.getBook());
        assertEquals(newDate, reservation.getReservationDate());
    }

    @Test
    void testIdIsNull() {
        assertNull(reservation.getId());
    }

    @Test
    void testReservationDateNotNull() {
        assertNotNull(reservation.getReservationDate());
    }
}
