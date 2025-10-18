package com.example.libraryhub.controller;

import com.example.libraryhub.dto.UserProfileResponse;
import com.example.libraryhub.dto.UserProfileUpdateRequest;
import com.example.libraryhub.model.Admin;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.User;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.service.ProfileService;
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
    private final ProfileService profileService;

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

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        UserProfileResponse profile = profileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping(value = "/profile", consumes = "application/json")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserProfileUpdateRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        UserProfileResponse updated = profileService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Map<String, Object>>> getUserReservations(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<Reservation> reservations = reservationRepository.findWithBookByUser_Username(userDetails.getUsername());

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}
