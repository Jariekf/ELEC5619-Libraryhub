package com.example.libraryhub.service;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.repository.BookRepository;
import com.example.libraryhub.repository.ReservationRepository;
import com.example.libraryhub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.*;

/**
 * Recommend up to 5 books by iterating user's recent high-rated (>=4) books.
 * Author-first, then genre; skip books already reviewed or reserved by the user.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    // Public API: recommend at most 5 books
    public List<Book> recommendTop5ByAuthorsAndGenres(String username) {
        // 1) Load user's high-rated books ordered by review time DESC
        List<Book> liked = reviewRepository.findRecentHighRatedBooksByUser(username);
        if (liked.isEmpty()) return Collections.emptyList();

        // 2) Build exclusion set: already reviewed + already reserved
        Set<Long> reviewedIds = new HashSet<>(reviewRepository.findReviewedBookIdsByUsername(username));
        Set<Long> reservedIds = new HashSet<>(reservationRepository.findReservedBookIdsByUsername(username));
        Set<Long> exclude = new HashSet<>();
        exclude.addAll(reviewedIds);
        exclude.addAll(reservedIds);

        // 3) Collect results with order preserved and no duplicates
        LinkedHashSet<Book> result = new LinkedHashSet<>();

        // 4) Iterate liked books by recency: author-first, then genre
        for (Book src : liked) {
            if (result.size() >= 5) break;

            // Author-first: fetch a slightly larger batch to bias author share
            if (src.getAuthor() != null) {
                var sameAuthor = bookRepository.findByAuthorAndIdNot(
                        src.getAuthor(), src.getId(), PageRequest.of(0, 5));
                for (Book b : sameAuthor) {
                    if (result.size() >= 5) break;
                    if (!exclude.contains(b.getId())) {
                        result.add(b);
                    }
                }
            }

            // Genre-second: fetch a smaller batch so author > genre share
            if (src.getGenre() != null && result.size() < 5) {
                var sameGenre = bookRepository.findByGenreAndIdNot(
                        src.getGenre(), src.getId(), PageRequest.of(0, 3));
                for (Book b : sameGenre) {
                    if (result.size() >= 5) break;
                    if (!exclude.contains(b.getId())) {
                        result.add(b);
                    }
                }
            }
        }

        // 5) Cap to 5 and return
        List<Book> list = new ArrayList<>(result);
        return list.subList(0, Math.min(5, list.size()));
    }
}
