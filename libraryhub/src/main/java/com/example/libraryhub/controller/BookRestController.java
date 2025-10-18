package com.example.libraryhub.controller;

import com.example.libraryhub.dto.BookDTO;
import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(BookDTO.fromEntity(book));
    }

    @PostMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String title) {
        List<Book> books = bookService.searchByTitle(title);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> addBook(@RequestParam String title,
                                           @RequestParam String author,
                                           @RequestParam String genre,
                                           @RequestParam(defaultValue = "true") boolean available) {
        Book book = new Book(title, author, genre);
        book.setAvailable(available);
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(BookDTO.fromEntity(savedBook));
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id,
                                              @RequestParam String title,
                                              @RequestParam String author,
                                              @RequestParam String genre,
                                              @RequestParam boolean available) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setAvailable(available);
        Book updatedBook = bookService.saveBook(book);
        return ResponseEntity.ok(BookDTO.fromEntity(updatedBook));
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Book not found");
            return ResponseEntity.notFound().build();
        }

        try {
            bookService.deleteBook(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book deleted successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete book");
            error.put("status", "error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBookRestful(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            bookService.deleteBook(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book deleted successfully");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete book");
            error.put("status", "error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}