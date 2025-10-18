package com.example.libraryhub.controller;

import com.example.libraryhub.dto.ReviewDTO;
import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Review;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class ReviewRestController {

    private final ReviewService reviewService;
    private final BookService bookService;
    private final UserRepository userRepository;

    /**
     * GET /api/books/{bookId}/reviews - Get all reviews for a book
     */
    @GetMapping("/api/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsForBook(@PathVariable Long bookId) {
        try {
            System.out.println("GET /api/books/" + bookId + "/reviews called");
            List<Review> reviews = reviewService.getReviewsForBook(bookId);
            System.out.println("Found " + reviews.size() + " reviews");
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(ReviewDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reviewDTOs);
        } catch (Exception e) {
            System.err.println("Error getting reviews: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * POST /api/books/{bookId}/reviews - Submit a review
     */
    @PostMapping("/api/books/{bookId}/reviews")
    public ResponseEntity<ReviewDTO> submitReview(
            @PathVariable Long bookId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Book book = bookService.getBookById(bookId);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }

            // Create review using the existing Review constructor
            Review review = new Review(user.getUsername(), comment, rating, book);
            Review savedReview = reviewService.saveReview(review);
            return ResponseEntity.ok(ReviewDTO.fromEntity(savedReview));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * DELETE /api/reviews/{reviewId} - Delete a review
     */
    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }

            // Get review using Optional handling
            var reviewOpt = reviewService.getReviewById(reviewId);
            if (reviewOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Review review = reviewOpt.get();

            // Check if user owns the review or is admin
            User currentUser = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean isOwner = review.getUsername().equals(currentUser.getUsername());
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (!isOwner && !isAdmin) {
                return ResponseEntity.status(403).build();
            }

            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}