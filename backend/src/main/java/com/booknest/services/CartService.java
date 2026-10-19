package com.booknest.services;

import com.booknest.dto.*;
import com.booknest.entities.*;
import com.booknest.exceptions.BadRequestException;
import com.booknest.exceptions.InsufficientStockException;
import com.booknest.exceptions.ResourceNotFoundException;
import com.booknest.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Cart getOrCreateUserCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                    return cartRepository.save(new Cart(user));
                });
    }

    @Transactional(readOnly = true)
    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateUserCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemDto> itemDtos = items.stream()
                .map(CartItemDto::from)
                .collect(Collectors.toList());
        return CartDto.of(cart.getId(), itemDtos);
    }

    @Transactional
    public CartDto addToCart(Long userId, AddToCartRequest req) {
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be a positive integer");
        }

        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + req.getBookId()));

        if (!book.isActive()) {
            throw new BadRequestException("This book is currently unavailable for purchase");
        }

        Cart cart = getOrCreateUserCart(userId);
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId());

        int newTotalQuantity = req.getQuantity();
        CartItem cartItem;

        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            newTotalQuantity += cartItem.getQuantity();
        } else {
            cartItem = new CartItem(cart, book, 0);
            if (cart.getItems() != null && !cart.getItems().contains(cartItem)) {
                cart.getItems().add(cartItem);
            }
        }

        if (newTotalQuantity > book.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Cannot add " + req.getQuantity() + " more item(s). Available stock is " +
                    book.getStockQuantity() + " (currently " + (existingItemOpt.isPresent() ? cartItem.getQuantity() : 0) + " in cart)");
        }

        cartItem.setQuantity(newTotalQuantity);
        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    @Transactional
    public CartDto updateItemQuantity(Long userId, Long itemId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateUserCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Unauthorized access to cart item");
        }

        Book book = item.getBook();
        if (newQuantity > book.getStockQuantity()) {
            throw new InsufficientStockException("Requested quantity (" + newQuantity + ") exceeds available stock (" + book.getStockQuantity() + ")");
        }

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);

        return getCart(userId);
    }

    @Transactional
    public CartDto removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateUserCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with ID: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Transactional
    public CartDto clearCart(Long userId) {
        Cart cart = getOrCreateUserCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(items);
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }
        return getCart(userId);
    }

    @Transactional
    public CartDto mergeGuestCart(Long userId, CartMergeRequest request) {
        if (request != null && request.getItems() != null && !request.getItems().isEmpty()) {
            for (AddToCartRequest itemReq : request.getItems()) {
                try {
                    addToCart(userId, itemReq);
                } catch (Exception ignored) {
                    // Skip or clamp if stock exhausted during merge
                }
            }
        }
        return getCart(userId);
    }
}
