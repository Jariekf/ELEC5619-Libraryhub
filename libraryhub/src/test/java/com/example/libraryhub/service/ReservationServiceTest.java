package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.WaitingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private WaitingListRepository waitingListRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Customer testUser;
    private Book testBook;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testUser = new Customer(1L, "testuser", "test@example.com", "hashedPassword");
        testBook = new Book("Test Book", "Test Author", "Fiction");
        testReservation = new Reservation(testUser, testBook);
    }

    @Test
    void testCreateReservationSuccess() {
        testBook.setAvailable(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.createReservation(1L, "testuser");

        assertNotNull(result);
        assertFalse(testBook.isAvailable());
    }

    @Test
    void testCreateReservationUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> reservationService.createReservation(1L, "testuser")
        );

        assertEquals("User not found: testuser", exception.getMessage());
    }

    @Test
    void testCreateReservationBookNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> reservationService.createReservation(1L, "testuser")
        );

        assertEquals("Book not found: 1", exception.getMessage());
    }

    @Test
    void testCreateReservationBookNotAvailable() {
        testBook.setAvailable(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> reservationService.createReservation(1L, "testuser")
        );

        assertEquals("Book is not available for reservation.", exception.getMessage());
    }

    @Test
    void testGetReservationsForUser() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findWithBookByUser_Username("testuser")).thenReturn(reservations);

        List<Reservation> result = reservationService.getReservationsForUser("testuser");

        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findWithBookByUser_Username("testuser");
    }

    @Test
    void testGetAllReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findAllWithDetails()).thenReturn(reservations);

        List<Reservation> result = reservationService.getAllReservations();

        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAllWithDetails();
    }

    @Test
    void testGetReservationForBook() {
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.of(testReservation));

        Optional<Reservation> result = reservationService.getReservationForBook(1L);

        assertTrue(result.isPresent());
        assertEquals(testReservation, result.get());
    }

    @Test
    void testReturnBookNoWaitingList() {
        testBook.setAvailable(false);
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.of(testReservation));
        when(waitingListRepository.findFirstByBookIdOrderByJoinDateAsc(1L)).thenReturn(null);

        reservationService.returnBook(1L, "testuser");

        verify(reservationRepository, times(1)).delete(testReservation);
        verify(bookRepository, times(1)).save(testBook);
        assertTrue(testBook.isAvailable());
    }

    @Test
    void testReturnBookWithWaitingList() {
        testBook.setAvailable(false);
        Customer nextUser = new Customer(2L, "nextuser", "next@example.com", "hash");
        WaitingList waitingEntry = new WaitingList();
        waitingEntry.setUser(nextUser);
        waitingEntry.setBook(testBook);

        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.of(testReservation));
        when(waitingListRepository.findFirstByBookIdOrderByJoinDateAsc(1L)).thenReturn(waitingEntry);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        reservationService.returnBook(1L, "testuser");

        verify(reservationRepository, times(1)).delete(testReservation);
        verify(waitingListRepository, times(1)).delete(waitingEntry);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(bookRepository, times(1)).save(testBook);
        assertFalse(testBook.isAvailable());
    }

    @Test
    void testReturnBookNoActiveReservation() {
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> reservationService.returnBook(1L, "testuser")
        );

        assertEquals("No active reservation for book: 1", exception.getMessage());
    }

    @Test
    void testReturnBookWrongUser() {
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.of(testReservation));

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> reservationService.returnBook(1L, "wronguser")
        );

        assertEquals("You do not hold this reservation.", exception.getMessage());
    }
}
