package com.booknest;

import com.booknest.dto.AddToCartRequest;
import com.booknest.dto.CartDto;
import com.booknest.entities.Book;
import com.booknest.entities.Category;
import com.booknest.entities.Role;
import com.booknest.entities.User;
import com.booknest.exceptions.InsufficientStockException;
import com.booknest.repositories.BookRepository;
import com.booknest.repositories.CategoryRepository;
import com.booknest.repositories.UserRepository;
import com.booknest.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setup() {
        testUser = userRepository.save(new User(
                "cart.user." + System.currentTimeMillis() + "@test.com",
                "hash", "Cart User", null, Role.CUSTOMER
        ));

        Category cat = categoryRepository.save(new Category("Cart Category", "cart-cat-" + System.currentTimeMillis(), "Desc"));

        Book b = new Book();
        b.setCategory(cat);
        b.setTitle("Cart Book Test");
        b.setAuthor("Author Cart");
        b.setIsbn("978-0" + String.format("%09d", Math.abs(System.nanoTime() % 1000000000L)));
        b.setDescription("Description");
        b.setPrice(new BigDecimal("20.00"));
        b.setStockQuantity(5);
        b.setActive(true);
        testBook = bookRepository.save(b);
    }

    @Test
    void testAddToCartAndCalculateTotals() {
        CartDto cart = cartService.addToCart(testUser.getId(), new AddToCartRequest(testBook.getId(), 2));

        assertNotNull(cart);
        assertEquals(2, cart.getItemCount());
        // 2 * $20.00 = $40.00 subtotal
        assertEquals(new BigDecimal("40.00"), cart.getSubtotal());
        // 5% tax = $2.00
        assertEquals(new BigDecimal("2.00"), cart.getTax());
        // subtotal < $50 -> shipping = $4.99
        assertEquals(new BigDecimal("4.99"), cart.getShippingFee());
        // total = 40 + 2 + 4.99 = 46.99
        assertEquals(new BigDecimal("46.99"), cart.getTotalAmount());
    }

    @Test
    void testAddToCartExceedingStockThrowsException() {
        // testBook stock is 5; requesting 6 must throw InsufficientStockException
        assertThrows(InsufficientStockException.class, () ->
                cartService.addToCart(testUser.getId(), new AddToCartRequest(testBook.getId(), 6))
        );
    }

    @Test
    void testFreeShippingWhenSubtotalReachesFiftyDollars() {
        // Add 3 * $20 = $60 subtotal
        CartDto cart = cartService.addToCart(testUser.getId(), new AddToCartRequest(testBook.getId(), 3));

        assertEquals(new BigDecimal("60.00"), cart.getSubtotal());
        assertEquals(new BigDecimal("0.00"), cart.getShippingFee());
    }
}
