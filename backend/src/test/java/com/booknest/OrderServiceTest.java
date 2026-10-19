package com.booknest;

import com.booknest.dto.AddToCartRequest;
import com.booknest.dto.CreateOrderRequest;
import com.booknest.dto.OrderDto;
import com.booknest.entities.*;
import com.booknest.exceptions.UnauthorizedException;
import com.booknest.repositories.BookRepository;
import com.booknest.repositories.CategoryRepository;
import com.booknest.repositories.UserRepository;
import com.booknest.services.CartService;
import com.booknest.services.OrderService;
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
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User customer1;
    private User customer2;
    private Book orderBook;

    @BeforeEach
    void setup() {
        customer1 = userRepository.save(new User(
                "buyer1." + System.currentTimeMillis() + "@test.com",
                "hash", "Buyer One", "+15551111", Role.CUSTOMER
        ));

        customer2 = userRepository.save(new User(
                "buyer2." + System.currentTimeMillis() + "@test.com",
                "hash", "Buyer Two", "+15552222", Role.CUSTOMER
        ));

        Category cat = categoryRepository.save(new Category("Order Cat", "order-cat-" + System.currentTimeMillis(), "Desc"));

        Book b = new Book();
        b.setCategory(cat);
        b.setTitle("Atomic Stock Book");
        b.setAuthor("Author Test");
        b.setIsbn("978-1" + String.format("%09d", Math.abs(System.nanoTime() % 1000000000L)));
        b.setDescription("Stock deduction verification book");
        b.setPrice(new BigDecimal("30.00"));
        b.setStockQuantity(10); // Initial stock = 10
        b.setActive(true);
        orderBook = bookRepository.save(b);
    }

    @Test
    void testOrderPlacementReducesStockAndClearsCart() {
        // Customer 1 adds 3 copies to cart
        cartService.addToCart(customer1.getId(), new AddToCartRequest(orderBook.getId(), 3));

        CreateOrderRequest orderReq = new CreateOrderRequest();
        orderReq.setCustomerName("Buyer One");
        orderReq.setCustomerPhone("+1 (555) 111-2222");
        orderReq.setDeliveryAddress("123 Maple Street");
        orderReq.setCity("Seattle");
        orderReq.setState("WA");
        orderReq.setPostalCode("98101");
        orderReq.setPaymentMethod(PaymentMethod.COD);

        OrderDto order = orderService.createOrder(customer1.getId(), orderReq);

        assertNotNull(order);
        assertNotNull(order.getOrderNumber());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(PaymentMethod.COD, order.getPaymentMethod());

        // Verify stock reduced from 10 to 7
        Book updatedBook = bookRepository.findById(orderBook.getId()).orElseThrow();
        assertEquals(7, updatedBook.getStockQuantity());

        // Verify cart is cleared
        assertEquals(0, cartService.getCart(customer1.getId()).getItemCount());

        // Verify snapshots preserved
        assertEquals(1, order.getItems().size());
        assertEquals("Atomic Stock Book", order.getItems().get(0).getBookTitleSnapshot());
        assertEquals(new BigDecimal("30.00"), order.getItems().get(0).getUnitPriceSnapshot());
    }

    @Test
    void testOrderCancellationRestoresInventory() {
        cartService.addToCart(customer1.getId(), new AddToCartRequest(orderBook.getId(), 4));

        CreateOrderRequest orderReq = new CreateOrderRequest();
        orderReq.setCustomerName("Buyer One");
        orderReq.setCustomerPhone("+1 555 1111");
        orderReq.setDeliveryAddress("456 Oak St");
        orderReq.setCity("Austin");
        orderReq.setState("TX");
        orderReq.setPostalCode("78701");
        orderReq.setPaymentMethod(PaymentMethod.COD);

        OrderDto order = orderService.createOrder(customer1.getId(), orderReq);

        // Stock was 10, reduced to 6
        assertEquals(6, bookRepository.findById(orderBook.getId()).orElseThrow().getStockQuantity());

        // Cancel order
        OrderDto cancelled = orderService.cancelOrder(customer1.getId(), order.getId(), false);
        assertEquals(OrderStatus.CANCELLED, cancelled.getOrderStatus());

        // Stock must be restored back to 10
        assertEquals(10, bookRepository.findById(orderBook.getId()).orElseThrow().getStockQuantity());
    }

    @Test
    void testCustomerCannotAccessAnotherCustomersOrder() {
        cartService.addToCart(customer1.getId(), new AddToCartRequest(orderBook.getId(), 1));

        CreateOrderRequest orderReq = new CreateOrderRequest();
        orderReq.setCustomerName("Buyer One");
        orderReq.setCustomerPhone("+1 555 1111");
        orderReq.setDeliveryAddress("Address 1");
        orderReq.setCity("City");
        orderReq.setState("State");
        orderReq.setPostalCode("12345");
        orderReq.setPaymentMethod(PaymentMethod.COD);

        OrderDto order = orderService.createOrder(customer1.getId(), orderReq);

        // Customer 2 attempts to view Customer 1's order -> must be denied with UnauthorizedException
        assertThrows(UnauthorizedException.class, () ->
                orderService.getOrderById(customer2.getId(), order.getId(), false)
        );
    }
}
