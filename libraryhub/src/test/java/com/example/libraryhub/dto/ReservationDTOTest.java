package com.example.libraryhub.dto;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Reservation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDTOTest {

    private void setId(Object obj, Long id) {
        try {
            Field field = obj.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            try {
                Field field = obj.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(obj, id);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Test
    void testGettersAndSetters() {
        ReservationDTO dto = new ReservationDTO();
        LocalDateTime date = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setReservationDate(date);
        
        assertEquals(1L, dto.getId());
        assertEquals(date, dto.getReservationDate());
    }

    @Test
    void testBookDTOGettersAndSetters() {
        ReservationDTO.BookDTO bookDTO = new ReservationDTO.BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Test Book");
        bookDTO.setAuthor("Test Author");
        bookDTO.setGenre("Fiction");
        bookDTO.setAvailable(true);

        assertEquals(1L, bookDTO.getId());
        assertEquals("Test Book", bookDTO.getTitle());
        assertEquals("Test Author", bookDTO.getAuthor());
        assertEquals("Fiction", bookDTO.getGenre());
        assertTrue(bookDTO.isAvailable());
    }

    @Test
    void testUserDTOGettersAndSetters() {
        ReservationDTO.UserDTO userDTO = new ReservationDTO.UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setRole("CUSTOMER");

        assertEquals(1L, userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("CUSTOMER", userDTO.getRole());
    }

    @Test
    void testFromEntityComplete() {
        // Create user
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        // Create book
        Book book = new Book("Test Book", "Test Author", "Fiction");
        setId(book, 100L);
        book.setAvailable(true);

        // Create reservation
        Reservation reservation = new Reservation(user, book);
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        // Convert to DTO
        ReservationDTO dto = ReservationDTO.fromEntity(reservation);

        // Verify reservation fields
        assertEquals(10L, dto.getId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), dto.getReservationDate());

        // Verify book fields
        assertNotNull(dto.getBook());
        assertEquals(100L, dto.getBook().getId());
        assertEquals("Test Book", dto.getBook().getTitle());
        assertEquals("Test Author", dto.getBook().getAuthor());
        assertEquals("Fiction", dto.getBook().getGenre());
        assertTrue(dto.getBook().isAvailable());

        // Verify user fields
        assertNotNull(dto.getUser());
        assertEquals(1L, dto.getUser().getId());
        assertEquals("customer1", dto.getUser().getUsername());
        assertEquals("customer1@test.com", dto.getUser().getEmail());
        assertEquals("CUSTOMER", dto.getUser().getRole());
    }

    @Test
    void testFromEntityWithoutBook() {
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        Reservation reservation = new Reservation(user, null);
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        ReservationDTO dto = ReservationDTO.fromEntity(reservation);

        assertEquals(10L, dto.getId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), dto.getReservationDate());
        assertNull(dto.getBook());
        assertNotNull(dto.getUser());
    }

    @Test
    void testFromEntityWithoutUser() {
        Book book = new Book("Test Book", "Test Author", "Fiction");
        setId(book, 100L);
        book.setAvailable(false);

        Reservation reservation = new Reservation(null, book);
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        ReservationDTO dto = ReservationDTO.fromEntity(reservation);

        assertEquals(10L, dto.getId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), dto.getReservationDate());
        assertNotNull(dto.getBook());
        assertNull(dto.getUser());
    }

    @Test
    void testFromEntityWithUnavailableBook() {
        Book book = new Book("Test Book", "Test Author", "Fiction");
        setId(book, 100L);
        book.setAvailable(false);

        Reservation reservation = new Reservation(null, book);
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        ReservationDTO dto = ReservationDTO.fromEntity(reservation);

        assertNotNull(dto.getBook());
        assertFalse(dto.getBook().isAvailable());
    }

    @Test
    void testSetBookDTO() {
        ReservationDTO dto = new ReservationDTO();
        ReservationDTO.BookDTO bookDTO = new ReservationDTO.BookDTO();
        bookDTO.setId(1L);
        bookDTO.setTitle("Test");
        
        dto.setBook(bookDTO);
        
        assertNotNull(dto.getBook());
        assertEquals(1L, dto.getBook().getId());
    }

    @Test
    void testSetUserDTO() {
        ReservationDTO dto = new ReservationDTO();
        ReservationDTO.UserDTO userDTO = new ReservationDTO.UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("test");
        
        dto.setUser(userDTO);
        
        assertNotNull(dto.getUser());
        assertEquals(1L, dto.getUser().getId());
    }

    @Test
    void testFromEntityWithAdminUser() {
        Admin user = new Admin(2L, "admin", "admin@test.com", "hash");

        Reservation reservation = new Reservation(user, null);
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.now());

        ReservationDTO dto = ReservationDTO.fromEntity(reservation);

        assertNotNull(dto.getUser());
        assertEquals("ADMIN", dto.getUser().getRole());
    }
}
