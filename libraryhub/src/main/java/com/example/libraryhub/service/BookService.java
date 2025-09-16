package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() { return bookRepository.findAll(); }
    public Book getBookById(Long id) { return bookRepository.findById(id).orElse(null); }
    public Book saveBook(Book book) { return bookRepository.save(book); }
    public List<Book> searchByTitle(String title) { return bookRepository.findByTitleContainingIgnoreCase(title); }
}
