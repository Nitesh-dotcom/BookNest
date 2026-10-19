package com.booknest.repositories;

import com.booknest.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookIdAndApprovedTrueOrderByCreatedAtDesc(Long bookId);
    Page<Review> findByBookIdAndApprovedTrueOrderByCreatedAtDesc(Long bookId, Pageable pageable);
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);
    boolean existsByBookIdAndUserId(Long bookId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.approved = true")
    Double calculateAverageRatingForBook(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId AND r.approved = true")
    int countApprovedReviewsForBook(@Param("bookId") Long bookId);

    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
