package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    public void deleteById(Long bookId) {
        bookRepository.deleteById(bookId);
    }


    public BookService(BookRepository bookRepository, 
                      ReservationRepository reservationRepository,
                      ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<Book> getAllBooks() { return bookRepository.findAll(); }
    public Book getBookById(Long id) { return bookRepository.findById(id).orElse(null); }
    public Book saveBook(Book book) { return bookRepository.save(book); }
    public List<Book> searchByTitle(String title) { return bookRepository.findByTitleContainingIgnoreCase(title); }
    
    @Transactional
    public void deleteBook(Long id) {
        // First delete all reservations for this book
        reservationRepository.deleteByBookId(id);
        
        // Then delete all reviews for this book
        reviewRepository.deleteByBookId(id);
        
        // Finally delete the book itself
        bookRepository.deleteById(id);
    }
}
