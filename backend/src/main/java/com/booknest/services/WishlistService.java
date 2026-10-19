package com.booknest.services;

import com.booknest.dto.AddToCartRequest;
import com.booknest.dto.AddToWishlistRequest;
import com.booknest.dto.WishlistDto;
import com.booknest.dto.WishlistItemDto;
import com.booknest.entities.Book;
import com.booknest.entities.User;
import com.booknest.entities.Wishlist;
import com.booknest.entities.WishlistItem;
import com.booknest.exceptions.BadRequestException;
import com.booknest.exceptions.ResourceNotFoundException;
import com.booknest.repositories.BookRepository;
import com.booknest.repositories.UserRepository;
import com.booknest.repositories.WishlistItemRepository;
import com.booknest.repositories.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public WishlistService(WishlistRepository wishlistRepository,
                           WishlistItemRepository wishlistItemRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository,
                           CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                    return wishlistRepository.save(new Wishlist(user));
                });
    }

    @Transactional(readOnly = true)
    public WishlistDto getWishlist(Long userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        List<WishlistItemDto> dtos = wishlist.getItems().stream()
                .map(WishlistItemDto::from)
                .collect(Collectors.toList());
        return new WishlistDto(wishlist.getId(), dtos);
    }

    @Transactional
    public WishlistDto addToWishlist(Long userId, AddToWishlistRequest request) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));

        if (!wishlistItemRepository.existsByWishlistIdAndBookId(wishlist.getId(), book.getId())) {
            WishlistItem item = new WishlistItem(wishlist, book);
            wishlistItemRepository.save(item);
        }

        return getWishlist(userId);
    }

    @Transactional
    public WishlistDto removeFromWishlist(Long userId, Long bookId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        wishlistItemRepository.deleteByWishlistIdAndBookId(wishlist.getId(), bookId);
        return getWishlist(userId);
    }

    @Transactional
    public void moveToCart(Long userId, Long bookId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        if (!wishlistItemRepository.existsByWishlistIdAndBookId(wishlist.getId(), bookId)) {
            throw new BadRequestException("Book is not in your wishlist");
        }

        // Add to cart
        cartService.addToCart(userId, new AddToCartRequest(bookId, 1));

        // Remove from wishlist
        wishlistItemRepository.deleteByWishlistIdAndBookId(wishlist.getId(), bookId);
    }
}
