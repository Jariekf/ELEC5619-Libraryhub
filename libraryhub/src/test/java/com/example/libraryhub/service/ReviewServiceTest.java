package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Review;
import com.example.libraryhub.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Book", "Test Author", "Fiction");
        testReview = new Review("testuser", "Great book!", 5, testBook);
    }

    @Test
    void testGetReviewsForBook() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByBookId(1L)).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsForBook(1L);

        assertEquals(1, result.size());
        assertEquals("Great book!", result.get(0).getComment());
        verify(reviewRepository, times(1)).findByBookId(1L);
    }

    @Test
    void testSaveReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.saveReview(testReview);

        assertNotNull(result);
        assertEquals("Great book!", result.getComment());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void testGetReviewById() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        Optional<Review> result = reviewService.getReviewById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void testGetReviewByIdNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.getReviewById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteReview() {
        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCanUserDeleteReviewTrue() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        boolean result = reviewService.canUserDeleteReview(1L, "testuser");

        assertTrue(result);
    }

    @Test
    void testCanUserDeleteReviewFalse() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        boolean result = reviewService.canUserDeleteReview(1L, "anotheruser");

        assertFalse(result);
    }

    @Test
    void testCanUserDeleteReviewNotFound() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = reviewService.canUserDeleteReview(999L, "testuser");

        assertFalse(result);
    }

    @Test
    void testGetAverageRatingForBook() {
        Review review1 = new Review("user1", "Good", 4, testBook);
        Review review2 = new Review("user2", "Excellent", 5, testBook);
        Review review3 = new Review("user3", "Average", 3, testBook);
        
        List<Review> reviews = Arrays.asList(review1, review2, review3);
        when(reviewRepository.findByBookId(1L)).thenReturn(reviews);

        double result = reviewService.getAverageRatingForBook(1L);

        assertEquals(4.0, result, 0.01);
    }

    @Test
    void testGetAverageRatingForBookNoReviews() {
        when(reviewRepository.findByBookId(1L)).thenReturn(Collections.emptyList());

        double result = reviewService.getAverageRatingForBook(1L);

        assertEquals(0.0, result, 0.01);
    }

    @Test
    void testGetAverageRatingWithNullRating() {
        Review review1 = new Review("user1", "Good", 4, testBook);
        Review review2 = new Review("user2", "No rating", null, testBook);
        
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewRepository.findByBookId(1L)).thenReturn(reviews);

        double result = reviewService.getAverageRatingForBook(1L);

        assertEquals(2.0, result, 0.01);
    }
}
