package com.booknest.repositories;

import com.booknest.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    List<Book> findByFeaturedTrueAndActiveTrue(Pageable pageable);
    List<Book> findByBestsellerTrueAndActiveTrue(Pageable pageable);
    List<Book> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.category.id = :categoryId AND b.id <> :excludeId AND b.active = true")
    List<Book> findRelatedBooks(@Param("categoryId") Long categoryId, @Param("excludeId") Long excludeId, Pageable pageable);

    long countByActiveTrue();
    long countByStockQuantityGreaterThan(int minStock);
    long countByStockQuantityEquals(int stock);

    @Query("SELECT SUM(b.stockQuantity) FROM Book b WHERE b.active = true")
    Long sumTotalActiveStock();
}
