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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Book", "Test Author", "Fiction");
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = Arrays.asList(testBook, new Book("Book 2", "Author 2", "Drama"));
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Book result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookByIdNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        Book result = bookService.getBookById(999L);

        assertNull(result);
        verify(bookRepository, times(1)).findById(999L);
    }

    @Test
    void testSaveBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.saveBook(testBook);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void testSearchByTitle() {
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(books);

        List<Book> result = bookService.searchByTitle("Test");

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("Test");
    }

    @Test
    void testDeleteBook() {
        Long bookId = 1L;

        bookService.deleteBook(bookId);

        verify(reservationRepository, times(1)).deleteByBookId(bookId);
        verify(reviewRepository, times(1)).deleteByBookId(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void testDeleteById() {
        Long bookId = 1L;

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }
}
