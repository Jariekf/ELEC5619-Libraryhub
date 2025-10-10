package com.example.libraryhub.controller;

import com.example.libraryhub.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Enhanced ChatbotController supporting:
 * - FR-23: Chat Interface with natural language interaction
 * - User context for personalized responses
 * - Error handling and logging
 */
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String message = payload.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Message cannot be empty")
                );
            }
            
            // Since chatbot requires authentication, userDetails should never be null
            String username = userDetails != null ? userDetails.getUsername() : "anonymous";
            log.info("Chatbot request from user: {}, message: {}", username, message);
            
            // If somehow userDetails is null (shouldn't happen with authentication required), return error
            if (userDetails == null) {
                return ResponseEntity.status(401).body(
                    Map.of("error", "Authentication required to use chatbot")
                );
            }
            
            String response = chatbotService.getResponse(message, username);
            
            return ResponseEntity.ok(Map.of(
                "response", response,
                "user", username != null ? username : "guest"
            ));
        } catch (Exception e) {
            log.error("Error processing chatbot request: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Sorry, I'm having trouble right now. Please try again later.")
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "LibraryHub AI Assistant",
            "capabilities", "FAQ, Book Search, Series Recommendations, General Assistance"
        ));
    }
}
