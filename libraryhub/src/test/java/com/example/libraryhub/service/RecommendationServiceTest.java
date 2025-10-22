package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private Book likedBook;
    private Book recommendedBook1;

    @BeforeEach
    void setUp() {
        likedBook = new Book("Liked Book", "Author A", "Fantasy");
        recommendedBook1 = new Book("Recommended 1", "Author A", "Fantasy");
    }
    
    private void setBookId(Book book, Long id) throws Exception {
        Field idField = Book.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(book, id);
    }

    @Test
    void testRecommendTop5NoLikedBooks() {
        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Collections.emptyList());

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertTrue(recommendations.isEmpty());
        verify(reviewRepository, times(1)).findRecentHighRatedBooksByUser("testuser");
    }

    @Test
    void testRecommendTop5WithLikedBooks() {
        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Arrays.asList(likedBook));
        when(reviewRepository.findReviewedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(1L));
        when(reservationRepository.findReservedBookIdsByUsername("testuser")).thenReturn(Collections.emptyList());
        when(bookRepository.findByAuthorAndIdNot(eq("Author A"), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(recommendedBook1));
        when(bookRepository.findByGenreAndIdNot(eq("Fantasy"), any(), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        verify(bookRepository, times(1)).findByAuthorAndIdNot(eq("Author A"), any(), any(PageRequest.class));
    }

    @Test
    void testRecommendTop5ExcludesReviewedBooks() throws Exception {
        setBookId(recommendedBook1, 2L);
        
        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Arrays.asList(likedBook));
        when(reviewRepository.findReviewedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(1L, 2L));
        when(reservationRepository.findReservedBookIdsByUsername("testuser")).thenReturn(Collections.emptyList());
        when(bookRepository.findByAuthorAndIdNot(any(), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(recommendedBook1));

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testRecommendTop5ExcludesReservedBooks() throws Exception {
        setBookId(recommendedBook1, 2L);
        
        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Arrays.asList(likedBook));
        when(reviewRepository.findReviewedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(1L));
        when(reservationRepository.findReservedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(2L));
        when(bookRepository.findByAuthorAndIdNot(any(), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(recommendedBook1));

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testRecommendTop5LimitsToFive() throws Exception {
        Book book1 = new Book("Book 1", "Author A", "Fantasy");
        Book book2 = new Book("Book 2", "Author A", "Fantasy");
        Book book3 = new Book("Book 3", "Author A", "Fantasy");
        Book book4 = new Book("Book 4", "Author A", "Fantasy");
        Book book5 = new Book("Book 5", "Author A", "Fantasy");
        Book book6 = new Book("Book 6", "Author A", "Fantasy");
        
        setBookId(book1, 10L);
        setBookId(book2, 11L);
        setBookId(book3, 12L);
        setBookId(book4, 13L);
        setBookId(book5, 14L);
        setBookId(book6, 15L);

        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Arrays.asList(likedBook));
        when(reviewRepository.findReviewedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(1L));
        when(reservationRepository.findReservedBookIdsByUsername("testuser")).thenReturn(Collections.emptyList());
        when(bookRepository.findByAuthorAndIdNot(any(), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(book1, book2, book3, book4, book5, book6));

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertEquals(5, recommendations.size());
    }

    @Test
    void testRecommendTop5ByGenre() {
        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Arrays.asList(likedBook));
        when(reviewRepository.findReviewedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(1L));
        when(reservationRepository.findReservedBookIdsByUsername("testuser")).thenReturn(Collections.emptyList());
        when(bookRepository.findByAuthorAndIdNot(any(), any(), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());
        when(bookRepository.findByGenreAndIdNot(eq("Fantasy"), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(recommendedBook1));

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        verify(bookRepository, times(1)).findByGenreAndIdNot(eq("Fantasy"), any(), any(PageRequest.class));
    }

    @Test
    void testRecommendTop5NoDuplicates() throws Exception {
        setBookId(recommendedBook1, 10L);
        
        when(reviewRepository.findRecentHighRatedBooksByUser("testuser")).thenReturn(Arrays.asList(likedBook));
        when(reviewRepository.findReviewedBookIdsByUsername("testuser")).thenReturn(Arrays.asList(1L));
        when(reservationRepository.findReservedBookIdsByUsername("testuser")).thenReturn(Collections.emptyList());
        when(bookRepository.findByAuthorAndIdNot(any(), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(recommendedBook1));
        when(bookRepository.findByGenreAndIdNot(any(), any(), any(PageRequest.class)))
                .thenReturn(Arrays.asList(recommendedBook1));

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres("testuser");

        assertEquals(1, recommendations.size());
    }
}
