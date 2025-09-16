package com.example.libraryhub.service;

import com.example.libraryhub.model.Review;
import com.example.libraryhub.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsForBook(Long bookId) { return reviewRepository.findByBookId(bookId); }
    public Review saveReview(Review review) { return reviewRepository.save(review); }
}
