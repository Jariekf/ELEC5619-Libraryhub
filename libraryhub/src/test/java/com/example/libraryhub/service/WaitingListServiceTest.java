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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingListServiceTest {

    @Mock
    private WaitingListRepository waitingListRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private WaitingListService waitingListService;

    private Customer testUser;
    private Book testBook;
    private WaitingList testWaitingList;

    @BeforeEach
    void setUp() {
        testUser = new Customer(1L, "testuser", "test@example.com", "hashedPassword");
        testBook = new Book("Test Book", "Test Author", "Fiction");
        testWaitingList = new WaitingList();
        testWaitingList.setBook(testBook);
        testWaitingList.setUser(testUser);
    }

    @Test
    void testAddToWaitingListSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.empty());
        when(waitingListRepository.existsByBookAndUser(testBook, testUser)).thenReturn(false);
        when(waitingListRepository.save(any(WaitingList.class))).thenReturn(testWaitingList);

        WaitingList result = waitingListService.addToWaitingList(1L, "testuser");

        assertNotNull(result);
        verify(waitingListRepository, times(1)).save(any(WaitingList.class));
    }

    @Test
    void testAddToWaitingListBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> waitingListService.addToWaitingList(1L, "testuser")
        );

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testAddToWaitingListUserNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> waitingListService.addToWaitingList(1L, "testuser")
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddToWaitingListAlreadyReserved() {
        Reservation reservation = new Reservation(testUser, testBook);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.of(reservation));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> waitingListService.addToWaitingList(1L, "testuser")
        );

        assertEquals("You have already reserved this book, cannot join waiting list.", exception.getMessage());
    }

    @Test
    void testAddToWaitingListAlreadyInWaitingList() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.empty());
        when(waitingListRepository.existsByBookAndUser(testBook, testUser)).thenReturn(true);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> waitingListService.addToWaitingList(1L, "testuser")
        );

        assertEquals("You are already on the waiting list for this book.", exception.getMessage());
    }

    @Test
    void testAddToWaitingListDataIntegrityViolation() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(reservationRepository.findByBookIdWithUser(1L)).thenReturn(Optional.empty());
        when(waitingListRepository.existsByBookAndUser(testBook, testUser)).thenReturn(false);
        when(waitingListRepository.save(any(WaitingList.class)))
            .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> waitingListService.addToWaitingList(1L, "testuser")
        );

        assertEquals("You are already on the waiting list for this book.", exception.getMessage());
    }

    @Test
    void testGetWaitingList() {
        List<WaitingList> waitingList = Arrays.asList(testWaitingList);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(waitingListRepository.findByBookOrderByJoinDateAsc(testBook)).thenReturn(waitingList);

        List<WaitingList> result = waitingListService.getWaitingList(1L);

        assertEquals(1, result.size());
        verify(waitingListRepository, times(1)).findByBookOrderByJoinDateAsc(testBook);
    }

    @Test
    void testGetWaitingListBookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> waitingListService.getWaitingList(999L)
        );

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testIsUserInWaitingListTrue() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(waitingListRepository.existsByBookAndUser(testBook, testUser)).thenReturn(true);

        boolean result = waitingListService.isUserInWaitingList(1L, "testuser");

        assertTrue(result);
    }

    @Test
    void testIsUserInWaitingListFalse() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(waitingListRepository.existsByBookAndUser(testBook, testUser)).thenReturn(false);

        boolean result = waitingListService.isUserInWaitingList(1L, "testuser");

        assertFalse(result);
    }

    @Test
    void testCancelWaiting() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        waitingListService.cancelWaiting(1L, "testuser");

        verify(waitingListRepository, times(1)).deleteByBookAndUser(testBook, testUser);
    }

    @Test
    void testDeleteWaitingEntry() {
        waitingListService.deleteWaitingEntry(1L);

        verify(waitingListRepository, times(1)).deleteById(1L);
    }
}
