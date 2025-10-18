package com.example.libraryhub.controller;

import com.example.libraryhub.dto.WaitingListDTO;
import com.example.libraryhub.model.WaitingList;
import com.example.libraryhub.service.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class WaitingListRestController {

    @Autowired
    private WaitingListService waitingListService;

    /**
     * GET /api/books/{bookId}/waitinglist - Get waiting list for a book
     */
    @GetMapping("/{bookId}/waitinglist")
    public ResponseEntity<List<WaitingListDTO>> getWaitingListForBook(@PathVariable Long bookId) {
        List<WaitingList> waitingList = waitingListService.getWaitingList(bookId);
        List<WaitingListDTO> dtos = waitingList.stream()
                .map(WaitingListDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /api/books/{bookId}/waitinglist - Join waiting list
     */
    @PostMapping("/{bookId}/waitinglist")
    public ResponseEntity<Map<String, String>> joinWaitingList(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Please login first"));
            }

            waitingListService.addToWaitingList(bookId, userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Joined waiting list successfully!");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * DELETE /api/books/{bookId}/waitinglist - Leave waiting list
     */
    @DeleteMapping("/{bookId}/waitinglist")
    public ResponseEntity<Map<String, String>> leaveWaitingList(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Please login first"));
            }

            waitingListService.cancelWaiting(bookId, userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Left waiting list successfully!");
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }
}