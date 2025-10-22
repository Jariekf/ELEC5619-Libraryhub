package com.example.libraryhub.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private Review review;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Test Book", "Test Author", "Fiction");
        review = new Review("testuser", "Great book!", 5, book);
    }

    @Test
    void testReviewConstructor() {
        assertEquals("testuser", review.getUsername());
        assertEquals("Great book!", review.getComment());
        assertEquals(5, review.getRating());
        assertEquals(book, review.getBook());
    }

    @Test
    void testDefaultConstructor() {
        Review emptyReview = new Review();
        assertNotNull(emptyReview);
    }

    @Test
    void testSettersAndGetters() {
        review.setUsername("newuser");
        review.setComment("Updated comment");
        review.setRating(4);

        assertEquals("newuser", review.getUsername());
        assertEquals("Updated comment", review.getComment());
        assertEquals(4, review.getRating());
    }

    @Test
    void testRatingRange() {
        review.setRating(1);
        assertEquals(1, review.getRating());

        review.setRating(5);
        assertEquals(5, review.getRating());
    }

    @Test
    void testBookAssociation() {
        Book newBook = new Book("Another Book", "Another Author", "Drama");
        review.setBook(newBook);
        assertEquals(newBook, review.getBook());
    }

    @Test
    void testIdIsNull() {
        assertNull(review.getId());
    }
}
