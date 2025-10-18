package com.example.libraryhub.dto;

import com.example.libraryhub.model.Review;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private String username;
    private Long bookId;

    public static ReviewDTO fromEntity(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .username(review.getUsername())
                .bookId(review.getBook() != null ? review.getBook().getId() : null)
                .build();
    }
}