package com.example.libraryhub.controller;

import com.example.libraryhub.model.User;
import com.example.libraryhub.model.Reservation;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.model.Role;
import com.example.libraryhub.service.BookService;
import com.example.libraryhub.service.ReservationService;
import com.example.libraryhub.service.WaitingListService;
import com.example.libraryhub.repository.UserRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.dto.ReservationDTO;
import com.example.libraryhub.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AdminRestController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    private final WaitingListService waitingListService;
    private final BookService bookService;

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCustomers", userRepository.count());
        stats.put("totalReservations", reservationService.getAllReservations().size());
        stats.put("totalBooks", bookService.getAllBooks().size());
        stats.put("totalWaitingListEntries", 0);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        List<ReservationDTO> dtos = reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {  // 注意返回类型是 CustomerDTO
        List<Reservation> allReservations = reservationRepository.findAllWithDetails();

        List<User> allUsers = userRepository.findAll();

        List<CustomerDTO> customers = allUsers.stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .map(user -> {
                    List<Reservation> userReservations = allReservations.stream()
                            .filter(r -> r.getUser().getId().equals(user.getId()))
                            .collect(Collectors.toList());
                    return CustomerDTO.fromEntity(user, userReservations);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCustomerById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/waitinglist/{entryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteWaitingEntry(@PathVariable Long entryId) {
        try {
            waitingListService.deleteWaitingEntry(entryId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Waiting list entry deleted successfully");
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", "error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long bookId) {
        try {
            bookService.deleteById(bookId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Book and its waiting list have been removed successfully");
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", "error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}