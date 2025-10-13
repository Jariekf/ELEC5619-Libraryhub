package com.example.libraryhub.controller;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping("/search")
    public List<Book> searchBooks(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }

    @PostMapping("/add")
    public Book addBook(@RequestParam String title,
                       @RequestParam String author,
                       @RequestParam String genre,
                       @RequestParam(defaultValue = "true") boolean available) {
        Book book = new Book(title, author, genre);
        book.setAvailable(available);
        return bookService.saveBook(book);
    }

    @PostMapping("/{id}/edit")
    public Book updateBook(@PathVariable Long id, 
                          @RequestParam String title,
                          @RequestParam String author,
                          @RequestParam String genre,
                          @RequestParam boolean available) {
        Book book = bookService.getBookById(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setAvailable(available);
        return bookService.saveBook(book);
    }

    @PostMapping("/{id}/delete")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}

