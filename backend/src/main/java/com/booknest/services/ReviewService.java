package com.booknest.services;

import com.booknest.dto.CreateReviewRequest;
import com.booknest.dto.ReviewDto;
import com.booknest.entities.Book;
import com.booknest.entities.Review;
import com.booknest.entities.User;
import com.booknest.exceptions.BadRequestException;
import com.booknest.exceptions.ResourceNotFoundException;
import com.booknest.repositories.BookRepository;
import com.booknest.repositories.OrderRepository;
import com.booknest.repositories.ReviewRepository;
import com.booknest.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookIdAndApprovedTrueOrderByCreatedAtDesc(bookId)
                .stream()
                .map(ReviewDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto createReview(Long userId, Long bookId, CreateReviewRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (reviewRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new BadRequestException("You have already submitted a review for this book.");
        }

        boolean verified = orderRepository.hasPurchasedBook(userId, bookId);

        Review review = new Review(
                book,
                user,
                request.getRating(),
                request.getHeadline(),
                request.getComment().trim(),
                verified
        );

        Review savedReview = reviewRepository.save(review);

        // Recalculate book's average rating & review count
        recalculateBookRating(book);

        return ReviewDto.from(savedReview);
    }

    @Transactional
    public void recalculateBookRating(Book book) {
        Double avg = reviewRepository.calculateAverageRatingForBook(book.getId());
        int count = reviewRepository.countApprovedReviewsForBook(book.getId());

        if (avg != null) {
            book.setAverageRating(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        } else {
            book.setAverageRating(BigDecimal.ZERO);
        }
        book.setReviewCount(count);
        bookRepository.save(book);
    }
}
