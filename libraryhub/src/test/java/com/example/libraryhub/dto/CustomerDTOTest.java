package com.example.libraryhub.dto;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Reservation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDTOTest {

    private void setId(Object obj, Long id) {
        try {
            Field field = obj.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            try {
                Field field = obj.getClass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(obj, id);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Test
    void testGettersAndSetters() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setRole("CUSTOMER");

        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("CUSTOMER", dto.getRole());
    }

    @Test
    void testReservationInfoGettersAndSetters() {
        CustomerDTO.ReservationInfo resInfo = new CustomerDTO.ReservationInfo();
        resInfo.setId(1L);
        LocalDateTime date = LocalDateTime.now();
        resInfo.setReservationDate(date);

        assertEquals(1L, resInfo.getId());
        assertEquals(date, resInfo.getReservationDate());
    }

    @Test
    void testBookInfoGettersAndSetters() {
        CustomerDTO.BookInfo bookInfo = new CustomerDTO.BookInfo();
        bookInfo.setId(1L);
        bookInfo.setTitle("Test Book");
        bookInfo.setAuthor("Test Author");
        bookInfo.setGenre("Fiction");

        assertEquals(1L, bookInfo.getId());
        assertEquals("Test Book", bookInfo.getTitle());
        assertEquals("Test Author", bookInfo.getAuthor());
        assertEquals("Fiction", bookInfo.getGenre());
    }

    @Test
    void testFromEntityWithReservations() {
        // Create user
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        // Create book
        Book book = new Book("Test Book", "Test Author", "Fiction");
        setId(book, 100L);

        // Create reservation
        Reservation reservation = new Reservation();
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));
        reservation.setBook(book);
        reservation.setUser(user);

        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        // Convert to DTO
        CustomerDTO dto = CustomerDTO.fromEntity(user, reservations);

        // Verify user fields
        assertEquals(1L, dto.getId());
        assertEquals("customer1", dto.getUsername());
        assertEquals("customer1@test.com", dto.getEmail());
        assertEquals("CUSTOMER", dto.getRole());

        // Verify reservations
        assertEquals(1, dto.getReservations().size());
        CustomerDTO.ReservationInfo resInfo = dto.getReservations().get(0);
        assertEquals(10L, resInfo.getId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), resInfo.getReservationDate());

        // Verify book info
        assertNotNull(resInfo.getBook());
        assertEquals(100L, resInfo.getBook().getId());
        assertEquals("Test Book", resInfo.getBook().getTitle());
        assertEquals("Test Author", resInfo.getBook().getAuthor());
        assertEquals("Fiction", resInfo.getBook().getGenre());
    }

    @Test
    void testFromEntityWithNoReservations() {
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        CustomerDTO dto = CustomerDTO.fromEntity(user, null);

        assertEquals(1L, dto.getId());
        assertEquals("customer1", dto.getUsername());
        assertEquals("customer1@test.com", dto.getEmail());
        assertEquals("CUSTOMER", dto.getRole());
        assertNotNull(dto.getReservations());
        assertTrue(dto.getReservations().isEmpty());
    }

    @Test
    void testFromEntityWithEmptyReservationsList() {
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        CustomerDTO dto = CustomerDTO.fromEntity(user, new ArrayList<>());

        assertEquals(1L, dto.getId());
        assertEquals("customer1", dto.getUsername());
        assertEquals("customer1@test.com", dto.getEmail());
        assertEquals("CUSTOMER", dto.getRole());
        assertNotNull(dto.getReservations());
        assertTrue(dto.getReservations().isEmpty());
    }

    @Test
    void testFromEntityWithMultipleReservations() {
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        Book book1 = new Book("Book 1", "Author 1", "Fiction");
        setId(book1, 100L);

        Book book2 = new Book("Book 2", "Author 2", "Non-Fiction");
        setId(book2, 200L);

        Reservation res1 = new Reservation();
        setId(res1, 10L);
        res1.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));
        res1.setBook(book1);

        Reservation res2 = new Reservation();
        setId(res2, 20L);
        res2.setReservationDate(LocalDateTime.of(2024, 2, 1, 12, 0));
        res2.setBook(book2);

        List<Reservation> reservations = List.of(res1, res2);

        CustomerDTO dto = CustomerDTO.fromEntity(user, reservations);

        assertEquals(2, dto.getReservations().size());
        assertEquals(10L, dto.getReservations().get(0).getId());
        assertEquals(20L, dto.getReservations().get(1).getId());
    }

    @Test
    void testFromEntityWithReservationWithoutBook() {
        Customer user = new Customer(1L, "customer1", "customer1@test.com", "hash");

        Reservation reservation = new Reservation();
        setId(reservation, 10L);
        reservation.setReservationDate(LocalDateTime.of(2024, 1, 1, 12, 0));
        reservation.setBook(null);  // No book

        List<Reservation> reservations = List.of(reservation);

        CustomerDTO dto = CustomerDTO.fromEntity(user, reservations);

        assertEquals(1, dto.getReservations().size());
        CustomerDTO.ReservationInfo resInfo = dto.getReservations().get(0);
        assertEquals(10L, resInfo.getId());
        assertNull(resInfo.getBook());
    }

    @Test
    void testFromEntityWithAdminRole() {
        Admin user = new Admin(2L, "admin", "admin@test.com", "hash");

        CustomerDTO dto = CustomerDTO.fromEntity(user, null);

        assertEquals(2L, dto.getId());
        assertEquals("admin", dto.getUsername());
        assertEquals("admin@test.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRole());
    }

    @Test
    void testSetReservations() {
        CustomerDTO dto = new CustomerDTO();
        
        CustomerDTO.ReservationInfo resInfo = new CustomerDTO.ReservationInfo();
        resInfo.setId(1L);
        resInfo.setReservationDate(LocalDateTime.now());
        
        List<CustomerDTO.ReservationInfo> reservations = List.of(resInfo);
        dto.setReservations(reservations);
        
        assertEquals(1, dto.getReservations().size());
        assertEquals(1L, dto.getReservations().get(0).getId());
    }

    @Test
    void testReservationInfoSetBook() {
        CustomerDTO.ReservationInfo resInfo = new CustomerDTO.ReservationInfo();
        CustomerDTO.BookInfo bookInfo = new CustomerDTO.BookInfo();
        bookInfo.setId(1L);
        bookInfo.setTitle("Test");
        
        resInfo.setBook(bookInfo);
        
        assertNotNull(resInfo.getBook());
        assertEquals(1L, resInfo.getBook().getId());
        assertEquals("Test", resInfo.getBook().getTitle());
    }
}
