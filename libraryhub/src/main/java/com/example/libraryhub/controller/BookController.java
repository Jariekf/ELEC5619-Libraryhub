package com.example.libraryhub.controller;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    public BookController(BookService bookService) { this.bookService = bookService; }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books"; // Thymeleaf template name: books.html
    }

    @GetMapping("/{id}")
    public String bookDetails(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "book-details"; // Thymeleaf template name: book-details.html
    }

    @PostMapping("/search")
    public String searchBooks(@RequestParam String title, Model model) {
        model.addAttribute("books", bookService.searchByTitle(title));
        return "books";
    }
}
