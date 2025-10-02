package com.example.libraryhub.service;

import com.example.libraryhub.model.Review;
import com.example.libraryhub.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewsForBook(Long bookId) { 
        return reviewRepository.findByBookId(bookId); 
    }
    
    public Review saveReview(Review review) { 
        return reviewRepository.save(review); 
    }
    
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }
    
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
    
    public boolean canUserDeleteReview(Long reviewId, String username) {
        Optional<Review> review = getReviewById(reviewId);
        return review.isPresent() && review.get().getUsername().equals(username);
    }
    
    public double getAverageRatingForBook(Long bookId) {
        List<Review> reviews = getReviewsForBook(bookId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(review -> review.getRating() != null ? review.getRating() : 0)
                .average()
                .orElse(0.0);
    }
}
