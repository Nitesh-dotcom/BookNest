package com.booknest.controllers;

import com.booknest.dto.ApiResponse;
import com.booknest.dto.CreateReviewRequest;
import com.booknest.dto.ReviewDto;
import com.booknest.security.UserPrincipal;
import com.booknest.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getReviews(@PathVariable Long bookId) {
        List<ReviewDto> reviews = reviewService.getReviewsForBook(bookId);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDto review = reviewService.createReview(principal.getId(), bookId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Review submitted successfully", review));
    }
}
