package com.example.libraryhub.repository;

import com.example.libraryhub.model.Book;
import com.example.libraryhub.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);

    // Recent high-rated (>=4) books by username, ordered by review id DESC as a recency proxy
    @Query("SELECT r.book FROM Review r " +
            "WHERE r.username = :username AND r.rating >= 4 " +
            "ORDER BY r.id DESC")
    List<Book> findRecentHighRatedBooksByUser(@Param("username") String username);

    // All reviewed book ids by username (for exclusion)
    @Query("SELECT DISTINCT r.book.id FROM Review r WHERE r.username = :username")
    List<Long> findReviewedBookIdsByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.book.id = :bookId")
    void deleteByBookId(Long bookId);
}
