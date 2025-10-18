package com.example.libraryhub.controller;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class RecommendationRestController {

    private final RecommendationService recommendationService;

    /**
     * GET /api/recommendations - Get personalized book recommendations
     */
    @GetMapping
    public ResponseEntity<List<Book>> getRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<Book> recommendations = recommendationService.recommendTop5ByAuthorsAndGenres(userDetails.getUsername());
        return ResponseEntity.ok(recommendations);
    }
}