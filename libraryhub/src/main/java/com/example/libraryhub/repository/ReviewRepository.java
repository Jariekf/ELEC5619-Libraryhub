package com.example.libraryhub.repository;

import com.example.libraryhub.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.book.id = :bookId")
    void deleteByBookId(Long bookId);
}
