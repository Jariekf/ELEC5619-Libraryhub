package com.example.libraryhub.controller;

import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class ReservationRestController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    /**
     * POST /api/books/{bookId}/reserve - Reserve a book
     */
    @PostMapping("/api/books/{bookId}/reserve")
    public ResponseEntity<Map<String, String>> reserveBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Please login first"));
            }

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Reservation reservation = reservationService.createReservation(bookId, userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Book reserved successfully!");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * DELETE /api/books/{bookId}/reserve - Cancel a reservation
     */
    @DeleteMapping("/api/books/{bookId}/reserve")
    public ResponseEntity<Map<String, String>> cancelReservation(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Please login first"));
            }

            reservationService.returnBook(bookId, userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Reservation cancelled successfully!");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/books/{bookId}/return - Return a book (same as cancel reservation)
     */
    @PostMapping("/api/books/{bookId}/return")
    public ResponseEntity<Map<String, String>> returnBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Please login first"));
            }

            reservationService.returnBook(bookId, userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Book returned successfully!");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/books/{bookId}/reservation - Get current reservation for a book (Admin only)
     */
    @GetMapping("/api/books/{bookId}/reservation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReservationForBook(@PathVariable Long bookId) {
        try {
            List<Reservation> allReservations = reservationService.getAllReservations();

            // Find reservation for this book
            Reservation reservation = allReservations.stream()
                    .filter(r -> r.getBook() != null && r.getBook().getId().equals(bookId))
                    .findFirst()
                    .orElse(null);

            if (reservation == null) {
                return ResponseEntity.notFound().build();
            }

            // Create response with reservation details
            Map<String, Object> response = new HashMap<>();
            response.put("id", reservation.getId());
            response.put("username", reservation.getUser() != null ? reservation.getUser().getUsername() : "Unknown");
            response.put("reservationDate", reservation.getReservationDate());
            response.put("bookId", reservation.getBook().getId());
            response.put("bookTitle", reservation.getBook().getTitle());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}