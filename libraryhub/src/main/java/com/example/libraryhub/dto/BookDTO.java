package com.example.libraryhub.dto;

import com.example.libraryhub.model.Book;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private String description;
    private boolean available;
    private Double averageRating;
    private Integer reviewCount;

    // Convert Entity to DTO
    public static BookDTO fromEntity(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .available(book.isAvailable())
                .averageRating(calculateAverageRating(book))
                .reviewCount(book.getReviews() != null ? book.getReviews().size() : 0)
                .build();
    }

    private static Double calculateAverageRating(Book book) {
        if (book.getReviews() == null || book.getReviews().isEmpty()) {
            return 0.0;
        }
        return book.getReviews().stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);
    }
}