package com.booknest.controllers;

import com.booknest.dto.AddToWishlistRequest;
import com.booknest.dto.ApiResponse;
import com.booknest.dto.WishlistDto;
import com.booknest.security.UserPrincipal;
import com.booknest.services.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        WishlistDto wishlist = wishlistService.getWishlist(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(wishlist));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddToWishlistRequest request) {
        WishlistDto wishlist = wishlistService.addToWishlist(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Book added to your wishlist", wishlist));
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<ApiResponse<WishlistDto>> removeFromWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long bookId) {
        WishlistDto wishlist = wishlistService.removeFromWishlist(principal.getId(), bookId);
        return ResponseEntity.ok(ApiResponse.ok("Book removed from wishlist", wishlist));
    }

    @PostMapping("/items/{bookId}/move-to-cart")
    public ResponseEntity<ApiResponse<WishlistDto>> moveToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long bookId) {
        wishlistService.moveToCart(principal.getId(), bookId);
        WishlistDto wishlist = wishlistService.getWishlist(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Book moved to cart", wishlist));
    }
}
