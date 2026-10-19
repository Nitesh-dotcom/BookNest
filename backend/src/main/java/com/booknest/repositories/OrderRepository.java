package com.booknest.repositories;

import com.booknest.entities.Order;
import com.booknest.entities.OrderStatus;
import com.booknest.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByIdAndUser(Long id, User user);
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Page<Order> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<Order> findTop5ByUserOrderByCreatedAtDesc(User user);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Order> findByOrderStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.orderStatus = :status) AND " +
           "(:query IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.user.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Order> searchOrders(@Param("status") OrderStatus status, @Param("query") String query, Pageable pageable);

    long countByOrderStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderStatus <> 'CANCELLED'")
    BigDecimal calculateTotalGrossRevenue();

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = :userId AND i.book.id = :bookId AND o.orderStatus = 'DELIVERED'")
    boolean hasPurchasedAndDelivered(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = :userId AND i.book.id = :bookId AND o.orderStatus <> 'CANCELLED'")
    boolean hasPurchasedBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
