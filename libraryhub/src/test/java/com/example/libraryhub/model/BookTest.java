package com.example.libraryhub.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Test Title", "Test Author", "Fiction");
    }

    @Test
    void testBookConstructor() {
        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Fiction", book.getGenre());
        assertTrue(book.isAvailable());
    }

    @Test
    void testDefaultConstructor() {
        Book emptyBook = new Book();
        assertNotNull(emptyBook);
    }

    @Test
    void testSettersAndGetters() {
        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setGenre("Science");
        book.setAvailable(false);

        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("Science", book.getGenre());
        assertFalse(book.isAvailable());
    }

    @Test
    void testAvailability() {
        assertTrue(book.isAvailable());
        book.setAvailable(false);
        assertFalse(book.isAvailable());
        book.setAvailable(true);
        assertTrue(book.isAvailable());
    }

    @Test
    void testReviewsList() {
        List<Review> reviews = new ArrayList<>();
        Review review = new Review();
        reviews.add(review);
        
        book.setReviews(reviews);
        
        assertNotNull(book.getReviews());
        assertEquals(1, book.getReviews().size());
    }

    @Test
    void testIdGeneration() {
        // ID should be null before persistence
        assertNull(book.getId());
    }
}
