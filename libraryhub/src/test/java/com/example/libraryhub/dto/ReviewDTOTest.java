package com.example.libraryhub.dto;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Review;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDTOTest {

    private void setId(Object obj, Long id) {
        try {
            Field field = obj.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testNoArgsConstructor() {
        ReviewDTO dto = new ReviewDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        ReviewDTO dto = new ReviewDTO(1L, 5, "Great book!", "user1", 100L);
        
        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getRating());
        assertEquals("Great book!", dto.getComment());
        assertEquals("user1", dto.getUsername());
        assertEquals(100L, dto.getBookId());
    }

    @Test
    void testBuilder() {
        ReviewDTO dto = ReviewDTO.builder()
                .id(1L)
                .rating(4)
                .comment("Good read")
                .username("user2")
                .bookId(200L)
                .build();
        
        assertEquals(1L, dto.getId());
        assertEquals(4, dto.getRating());
        assertEquals("Good read", dto.getComment());
        assertEquals("user2", dto.getUsername());
        assertEquals(200L, dto.getBookId());
    }

    @Test
    void testGettersAndSetters() {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(1L);
        dto.setRating(5);
        dto.setComment("Excellent!");
        dto.setUsername("testuser");
        dto.setBookId(100L);

        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getRating());
        assertEquals("Excellent!", dto.getComment());
        assertEquals("testuser", dto.getUsername());
        assertEquals(100L, dto.getBookId());
    }

    @Test
    void testFromEntityComplete() {
        Book book = new Book("Test Book", "Test Author", "Fiction");
        setId(book, 100L);

        Review review = new Review("reader1", "Amazing book!", 5, book);
        setId(review, 1L);

        ReviewDTO dto = ReviewDTO.fromEntity(review);

        assertEquals(1L, dto.getId());
        assertEquals(5, dto.getRating());
        assertEquals("Amazing book!", dto.getComment());
        assertEquals("reader1", dto.getUsername());
        assertEquals(100L, dto.getBookId());
    }

    @Test
    void testFromEntityWithoutBook() {
        Review review = new Review("reader1", "Good book", 4, null);
        setId(review, 1L);

        ReviewDTO dto = ReviewDTO.fromEntity(review);

        assertEquals(1L, dto.getId());
        assertEquals(4, dto.getRating());
        assertEquals("Good book", dto.getComment());
        assertEquals("reader1", dto.getUsername());
        assertNull(dto.getBookId());
    }

    @Test
    void testFromEntityWithMinimalRating() {
        Book book = new Book("Book", "Author", "Genre");
        setId(book, 100L);

        Review review = new Review("critic", "Not good", 1, book);
        setId(review, 1L);

        ReviewDTO dto = ReviewDTO.fromEntity(review);

        assertEquals(1, dto.getRating());
        assertEquals("Not good", dto.getComment());
    }

    @Test
    void testFromEntityWithEmptyComment() {
        Book book = new Book("Book", "Author", "Genre");
        setId(book, 100L);

        Review review = new Review("reader", "", 3, book);
        setId(review, 1L);

        ReviewDTO dto = ReviewDTO.fromEntity(review);

        assertEquals("", dto.getComment());
    }

    @Test
    void testFromEntityWithNullComment() {
        Book book = new Book("Book", "Author", "Genre");
        setId(book, 100L);

        Review review = new Review("reader", null, 3, book);
        setId(review, 1L);

        ReviewDTO dto = ReviewDTO.fromEntity(review);

        assertNull(dto.getComment());
    }

    @Test
    void testBuilderPartial() {
        ReviewDTO dto = ReviewDTO.builder()
                .rating(5)
                .username("user")
                .build();
        
        assertEquals(5, dto.getRating());
        assertEquals("user", dto.getUsername());
        assertNull(dto.getId());
        assertNull(dto.getComment());
        assertNull(dto.getBookId());
    }
}
