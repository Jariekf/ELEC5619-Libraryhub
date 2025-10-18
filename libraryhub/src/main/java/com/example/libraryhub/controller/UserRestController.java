package com.example.libraryhub.controller;

import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class UserRestController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user instanceof Admin ? "ADMIN" : "CUSTOMER");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Map<String, Object>>> getUserReservations(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        // Get all reservations for the current user using the existing repository method
        List<Reservation> reservations = reservationRepository.findWithBookByUser_Username(userDetails.getUsername());

        // Convert to response format
        List<Map<String, Object>> response = reservations.stream()
                .map(reservation -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", reservation.getId());
                    map.put("bookId", reservation.getBook().getId());
                    map.put("bookTitle", reservation.getBook().getTitle());
                    map.put("bookAuthor", reservation.getBook().getAuthor());
                    map.put("reservationDate", reservation.getReservationDate());
                    map.put("username", reservation.getUser().getUsername());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}