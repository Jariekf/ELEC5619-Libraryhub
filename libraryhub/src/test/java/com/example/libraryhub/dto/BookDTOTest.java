package com.example.libraryhub.dto;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Review;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDTOTest {

    @Test
    void testFromEntityWithNoReviews() {
        Book book = new Book("Test Book", "Test Author", "Fiction");
        book.setAvailable(true);
        book.setReviews(new ArrayList<>());

        BookDTO dto = BookDTO.fromEntity(book);

        assertEquals("Test Book", dto.getTitle());
        assertEquals("Test Author", dto.getAuthor());
        assertEquals("Fiction", dto.getGenre());
        assertTrue(dto.isAvailable());
        assertEquals(0.0, dto.getAverageRating());
        assertEquals(0, dto.getReviewCount());
    }

    @Test
    void testFromEntityWithReviews() {
        Book book = new Book("Test Book", "Test Author", "Fiction");
        book.setAvailable(false);
        
        List<Review> reviews = new ArrayList<>();
        Review review1 = new Review("user1", "Good", 4, book);
        Review review2 = new Review("user2", "Excellent", 5, book);
        reviews.add(review1);
        reviews.add(review2);
        book.setReviews(reviews);

        BookDTO dto = BookDTO.fromEntity(book);

        assertEquals("Test Book", dto.getTitle());
        assertFalse(dto.isAvailable());
        assertEquals(4.5, dto.getAverageRating(), 0.01);
        assertEquals(2, dto.getReviewCount());
    }

    @Test
    void testFromEntityWithNullReviews() {
        Book book = new Book("Test Book", "Test Author", "Fiction");
        book.setReviews(null);

        BookDTO dto = BookDTO.fromEntity(book);

        assertEquals(0.0, dto.getAverageRating());
        assertEquals(0, dto.getReviewCount());
    }

    @Test
    void testBuilderPattern() {
        BookDTO dto = BookDTO.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .genre("Fiction")
                .available(true)
                .averageRating(4.5)
                .reviewCount(10)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Test Book", dto.getTitle());
        assertEquals("Test Author", dto.getAuthor());
        assertEquals("Fiction", dto.getGenre());
        assertTrue(dto.isAvailable());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(10, dto.getReviewCount());
    }

    @Test
    void testNoArgsConstructor() {
        BookDTO dto = new BookDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        BookDTO dto = new BookDTO(1L, "Title", "Author", "Genre", "ISBN", "Description", true, 4.5, 10);
        
        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Author", dto.getAuthor());
        assertEquals("Genre", dto.getGenre());
        assertEquals("ISBN", dto.getIsbn());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.isAvailable());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(10, dto.getReviewCount());
    }

    @Test
    void testSettersAndGetters() {
        BookDTO dto = new BookDTO();
        
        dto.setId(1L);
        dto.setTitle("Title");
        dto.setAuthor("Author");
        dto.setGenre("Genre");
        dto.setIsbn("ISBN");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setAverageRating(4.5);
        dto.setReviewCount(10);

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Author", dto.getAuthor());
        assertEquals("Genre", dto.getGenre());
        assertEquals("ISBN", dto.getIsbn());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.isAvailable());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(10, dto.getReviewCount());
    }
}
