package com.example.libraryhub.dto;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Customer;
import com.example.libraryhub.model.WaitingList;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WaitingListDTOTest {

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
    void testNoArgsConstructor() {
        WaitingListDTO dto = new WaitingListDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 1, 12, 0);
        WaitingListDTO dto = new WaitingListDTO(1L, 100L, "Test Book", 10L, "user1", date);
        
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals(10L, dto.getUserId());
        assertEquals("user1", dto.getUsername());
        assertEquals(date, dto.getJoinedDate());
    }

    @Test
    void testGettersAndSetters() {
        WaitingListDTO dto = new WaitingListDTO();
        LocalDateTime date = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setBookId(100L);
        dto.setBookTitle("Test Book");
        dto.setUserId(10L);
        dto.setUsername("testuser");
        dto.setJoinedDate(date);

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals(10L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals(date, dto.getJoinedDate());
    }

    @Test
    void testFromEntityComplete() {
        Book book = new Book("Waiting Book", "Author", "Genre");
        setId(book, 100L);

        Customer user = new Customer(10L, "waiting_user", "wait@test.com", "hash");

        LocalDateTime joinDate = LocalDateTime.of(2024, 1, 15, 14, 30);
        
        WaitingList waitingList = new WaitingList();
        setId(waitingList, 1L);
        waitingList.setBook(book);
        waitingList.setUser(user);
        waitingList.setJoinDate(joinDate);

        WaitingListDTO dto = WaitingListDTO.fromEntity(waitingList);

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getBookId());
        assertEquals("Waiting Book", dto.getBookTitle());
        assertEquals(10L, dto.getUserId());
        assertEquals("waiting_user", dto.getUsername());
        assertEquals(joinDate, dto.getJoinedDate());
    }

    @Test
    void testFromEntityWithoutBook() {
        Customer user = new Customer(10L, "user1", "user1@test.com", "hash");

        WaitingList waitingList = new WaitingList();
        setId(waitingList, 1L);
        waitingList.setBook(null);
        waitingList.setUser(user);
        waitingList.setJoinDate(LocalDateTime.now());

        WaitingListDTO dto = WaitingListDTO.fromEntity(waitingList);

        assertEquals(1L, dto.getId());
        assertNull(dto.getBookId());
        assertNull(dto.getBookTitle());
        assertEquals(10L, dto.getUserId());
        assertEquals("user1", dto.getUsername());
    }

    @Test
    void testFromEntityWithoutUser() {
        Book book = new Book("Test Book", "Author", "Genre");
        setId(book, 100L);

        WaitingList waitingList = new WaitingList();
        setId(waitingList, 1L);
        waitingList.setBook(book);
        waitingList.setUser(null);
        waitingList.setJoinDate(LocalDateTime.now());

        WaitingListDTO dto = WaitingListDTO.fromEntity(waitingList);

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getBookId());
        assertEquals("Test Book", dto.getBookTitle());
        assertNull(dto.getUserId());
        assertEquals("Unknown", dto.getUsername());
    }

    @Test
    void testFromEntityWithNullBookAndUser() {
        WaitingList waitingList = new WaitingList();
        setId(waitingList, 1L);
        waitingList.setBook(null);
        waitingList.setUser(null);
        waitingList.setJoinDate(LocalDateTime.of(2024, 3, 1, 10, 0));

        WaitingListDTO dto = WaitingListDTO.fromEntity(waitingList);

        assertEquals(1L, dto.getId());
        assertNull(dto.getBookId());
        assertNull(dto.getBookTitle());
        assertNull(dto.getUserId());
        assertEquals("Unknown", dto.getUsername());
        assertEquals(LocalDateTime.of(2024, 3, 1, 10, 0), dto.getJoinedDate());
    }

    @Test
    void testFromEntityPreservesExactJoinDate() {
        Book book = new Book("Test", "Author", "Genre");
        setId(book, 100L);

        Customer user = new Customer(10L, "user", "user@test.com", "hash");

        LocalDateTime exactDate = LocalDateTime.of(2024, 6, 15, 13, 45, 30);
        
        WaitingList waitingList = new WaitingList();
        setId(waitingList, 1L);
        waitingList.setBook(book);
        waitingList.setUser(user);
        waitingList.setJoinDate(exactDate);

        WaitingListDTO dto = WaitingListDTO.fromEntity(waitingList);

        assertEquals(exactDate, dto.getJoinedDate());
    }
}
