package com.example.libraryhub.controller;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ReservationService reservationService;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book/books"; // Thymeleaf template name: books.html
    }

    @GetMapping("/{id}")
    public String bookDetails(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        if (book != null && !book.isAvailable()) {
            reservationService.getReservationForBook(id)
                    .ifPresent(reservation -> model.addAttribute("reservation", reservation));
        }
        return "book/book-details";
    }

    @PostMapping("/search")
    public String searchBooks(@RequestParam String title, Model model) {
        model.addAttribute("books", bookService.searchByTitle(title));
        return "book/books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "book/add-book"; // create add-book.html
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @PostMapping("/{id}/reserve")
    public String reserveBook(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            // This case should be handled by Spring Security, but it's a good safeguard.
            return "redirect:/auth/login";
        }
        try {
            reservationService.createReservation(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Book reserved successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not reserve book due to an unexpected error.");
        }
        return "redirect:/books/" + id;
    }
}
