package com.booknest.repositories;

import com.booknest.entities.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistIdAndBookId(Long wishlistId, Long bookId);
    boolean existsByWishlistIdAndBookId(Long wishlistId, Long bookId);
    void deleteByWishlistIdAndBookId(Long wishlistId, Long bookId);
}
