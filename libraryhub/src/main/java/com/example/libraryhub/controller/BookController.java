package com.example.libraryhub.controller;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Review;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.service.ReservationService;
import com.example.libraryhub.service.ReviewService;
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
    private final ReviewService reviewService;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book/books"; // Thymeleaf template name: books.html
    }

    @GetMapping("/{id}")
    public String bookDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewService.getReviewsForBook(id));
        model.addAttribute("averageRating", reviewService.getAverageRatingForBook(id));
        model.addAttribute("newReview", new Review());
        if (!book.isAvailable()) {
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

    @PostMapping("/{id}/reviews")
    public String submitReview(@PathVariable Long id,
                               @ModelAttribute("newReview") Review review,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/books";
        }
        
        // Validate rating
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Rating must be between 1 and 5 stars.");
            return "redirect:/books/" + id;
        }
        
        // Set the book and username
        review.setBook(book);
        review.setUsername(userDetails.getUsername());
        
        try {
            reviewService.saveReview(review);
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit review. Please try again.");
        }
        
        return "redirect:/books/" + id;
    }

    @PostMapping("/{bookId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long bookId,
                              @PathVariable Long reviewId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        
        if (!reviewService.canUserDeleteReview(reviewId, userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only delete your own reviews.");
            return "redirect:/books/" + bookId;
        }
        
        try {
            reviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete review. Please try again.");
        }
        
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/admin/books/{bookId}/reviews/{reviewId}/delete")
    public String adminDeleteReview(@PathVariable Long bookId,
                                   @PathVariable Long reviewId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/auth/login";
        }
        
        // Check if user has ADMIN role
        if (!userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only administrators can delete any review.");
            return "redirect:/books/" + bookId;
        }
        
        try {
            reviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully by administrator!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete review. Please try again.");
        }
        
        return "redirect:/books/" + bookId;
    }

    // Admin Book Management Endpoints
    
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, 
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Admin privileges required.");
            return "redirect:/books";
        }
        
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/books";
        }
        
        model.addAttribute("book", book);
        return "book/edit-book";
    }

    @PostMapping("/admin/edit/{id}")
    public String updateBook(@PathVariable Long id, 
                           @ModelAttribute("book") Book book,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Admin privileges required.");
            return "redirect:/books";
        }
        
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/books";
        }
        
        // Update the existing book with new details
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setGenre(book.getGenre());
        existingBook.setAvailable(book.isAvailable());
        
        try {
            bookService.saveBook(existingBook);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update book. Please try again.");
        }
        
        return "redirect:/books";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteBook(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null || !userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied. Admin privileges required.");
            return "redirect:/books";
        }
        
        Book book = bookService.getBookById(id);
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found.");
            return "redirect:/books";
        }
        
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book and all associated data deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete book. Please try again.");
        }
        
        return "redirect:/books";
    }
}
