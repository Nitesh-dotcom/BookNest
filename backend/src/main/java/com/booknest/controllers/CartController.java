package com.booknest.controllers;

import com.booknest.dto.AddToCartRequest;
import com.booknest.dto.ApiResponse;
import com.booknest.dto.CartDto;
import com.booknest.dto.CartMergeRequest;
import com.booknest.dto.UpdateCartItemRequest;
import com.booknest.security.UserPrincipal;
import com.booknest.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        CartDto cart = cartService.getCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddToCartRequest request) {
        CartDto cart = cartService.addToCart(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> updateItemQuantity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartDto cart = cartService.updateItemQuantity(principal.getId(), itemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long itemId) {
        CartDto cart = cartService.removeItem(principal.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CartDto>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        CartDto cart = cartService.clearCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", cart));
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiResponse<CartDto>> mergeGuestCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CartMergeRequest request) {
        CartDto cart = cartService.mergeGuestCart(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Cart merged successfully", cart));
    }
}
